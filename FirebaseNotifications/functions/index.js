'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();


/*
 * 'OnWrite' works as 'addValueEventListener' for android. It will fire the function
 * everytime there is some item added, removed or changed from the provided 'database.ref'
 * 'sendNotification' is the name of the function, which can be changed according to
 * your requirement
 */

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((data, context) => {


  /*
   * You can store values as variables from the 'database.ref'
   * Just like here, I've done for 'user_id' and 'notification'
   */

  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;

  
 

  });﻿