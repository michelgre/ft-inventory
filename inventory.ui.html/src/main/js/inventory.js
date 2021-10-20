import {RemoteApp} from '@eclipse-scout/core';
import * as inventory from './index';

Object.assign({}, inventory); // Use import so that it is not marked as unused

new RemoteApp().init();

