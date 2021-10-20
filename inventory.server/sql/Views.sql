-- View: v_box_contains

-- DROP VIEW v_box_contains;

CREATE OR REPLACE VIEW v_box_contains AS
 SELECT 'A' as level, b.container_id,
    NULL::integer AS bin_id,
    b.part_id,
    b.count
   FROM box_contains b
UNION
 SELECT 'B' as level, b.parentid AS container_id,
    bc.container_id AS bin_id,
    bc.part_id,
    bc.count
   FROM box b
     JOIN box_contains bc ON bc.container_id = b.id
  WHERE b.parentid > 0
UNION
 SELECT 'C' as level, b1.parentid AS container_id,
    bc.container_id AS bin_id,
    bc.part_id,
    bc.count
   FROM box b1
     JOIN box b2 ON b2.parentid = b1.id
     JOIN box_contains bc ON bc.container_id = b2.id;
     
ALTER TABLE v_box_contains
    OWNER TO michel;

GRANT ALL ON TABLE v_box_contains TO michel;
GRANT ALL ON TABLE v_box_contains TO ftdb;


CREATE OR REPLACE VIEW v_color_de AS
 SELECT c.id,
    c.label_id,
    l.label
   FROM color c
     JOIN multilingual_label l ON l.id = c.label_id
  WHERE l.langcode = 'de'::bpchar;

ALTER TABLE v_color_de
    OWNER TO michel;


CREATE OR REPLACE VIEW v_one_part_number AS
 SELECT DISTINCT ON (p.id) p.id AS part_id,
    pn.number,
    pn.year,
    (COALESCE(pn.year, ''::character varying)::text || ':'::text) || pn.number::text AS year_number
   FROM part p
     LEFT JOIN part_number pn ON pn.part_id = p.id AND pn.number IS NOT NULL
  ORDER BY p.id, COALESCE(pn.year, ''::character varying) DESC;

ALTER TABLE v_one_part_number
    OWNER TO michel;

GRANT ALL ON TABLE v_one_part_number TO michel;
GRANT ALL ON TABLE v_one_part_number TO ftdb;


CREATE OR REPLACE VIEW v_part_all_lang AS
 SELECT p.id,
    p.title_id,
    t.label AS title,
    p.ft_icon,
    p.ft_cat,
    p.color_id,
    cl.label AS color,
    pn.part_numbers,
    t.langcode
   FROM part p
     LEFT JOIN multilingual_label t ON t.id = p.title_id
     LEFT JOIN color c ON c.id = p.color_id
     LEFT JOIN multilingual_label cl ON cl.id = c.label_id AND cl.langcode = t.langcode
     LEFT JOIN v_part_numbers pn ON pn.part_id = p.id;

ALTER TABLE v_part_all_lang
    OWNER TO michel;


CREATE OR REPLACE VIEW v_part_de AS
 SELECT p.id,
    l.label
   FROM part p
     JOIN multilingual_label l ON l.id = p.title_id AND l.langcode = 'de'::bpchar;

ALTER TABLE v_part_de
    OWNER TO michel;


CREATE OR REPLACE VIEW v_part_numbers AS
 SELECT part_number.part_id,
    string_agg((COALESCE(part_number.year, ''::character varying)::text || ':'::text) || part_number.number::text, ','::text) AS part_numbers
   FROM part_number
  GROUP BY part_number.part_id;

ALTER TABLE v_part_numbers
    OWNER TO michel;

GRANT ALL ON TABLE v_part_numbers TO michel;
GRANT ALL ON TABLE v_part_numbers TO ftdb;

CREATE OR REPLACE VIEW v_parts_page AS
 SELECT p.id,
    pn.part_numbers,
    'icons/?image='::text || p.ft_icon AS part_icon,
    p.color_id,
    COALESCE(l1.label, l2.label) AS part_label,
    l2.label AS default_label,
    p.ft_cat,
    p.cost,
    ( SELECT sum(part_contains.count) AS sum
           FROM part_contains
          WHERE part_contains.container_id = p.id) AS kit_sum,
    inv_data.inv_sum,
    inv_data.inv_user_id
   FROM part p
     LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = 'fr'::bpchar
     LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = 'de'::bpchar
     LEFT JOIN v_part_numbers pn ON pn.part_id = p.id
     LEFT JOIN ( SELECT bc.part_id,
            b.user_id AS inv_user_id,
            sum(bc.count) AS inv_sum
           FROM box_contains bc
             JOIN box b ON bc.container_id = b.id
          WHERE NOT b.lot_achat AND NOT b.given
          GROUP BY b.user_id, bc.part_id) inv_data ON inv_data.part_id = p.id;

ALTER TABLE v_parts_page
    OWNER TO michel;

GRANT ALL ON TABLE v_parts_page TO michel;
GRANT ALL ON TABLE v_parts_page TO ftdb;

