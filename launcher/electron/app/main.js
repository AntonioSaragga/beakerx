/*
 *  Copyright 2015 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

var app = require('app');  // Module to control application life.
var Menu = require('menu');
var MenuItem = require('menu-item');
var BrowserWindow = require('browser-window');  // Module to create native browser window.
var ipc = require('ipc');
var http = require('http');

var path = require('path');
var events = require('events');
var eventEmitter = new events.EventEmitter();
var makeMenuTemplate = require('./default-menu-maker.js');

var java_home = path.resolve(__dirname + '/../jre/Contents/Home'); 
var backend;
var serverUrl;
var openFile;
var mainMenu;
var appReady = false;

var windowOptions = require('./window-options.js');

// Report crashes to our server.
require('crash-reporter').start();

ipc.on('quit', function() {
  app.quit();
});

ipc.on('try-change-server', function() {
  var popup = new BrowserWindow(windowOptions.popupOptions);
  popup.loadUrl(serverUrl + '/beaker/#/changeserver');
});

ipc.on('new-backend', function() {
  var windows = BrowserWindow.getAllWindows();
  for (var i = 0; i < windows.length; ++i){
    windows[i].close();
  }
  console.log('Killing backend');
  if ((!backend.dead) && (addr != serverUrl)){
    killBackend();
  }
  runBeaker();
});

ipc.on('change-server', function(event, addr){
  var windows = BrowserWindow.getAllWindows();
  for (var i = 0; i < windows.length; ++i){
    windows[i].close();
  }
  console.log('Killing backend');
  if ((!backend.dead) && (addr != serverUrl)){
    killBackend();
  }
  // Open new control panel there
  var newWindow = new BrowserWindow(windowOptions.defaultWindowOptions);
  console.log('Switching to ' + addr);
  newWindow.loadUrl(addr);
  newWindow.toggleDevTools();
  serverUrl = addr;
  MainMenu = Menu.buildFromTemplate(makeMenuTemplate(serverUrl));
});

// Kill backend before exiting 
app.on('quit', function() {
  exit();
});

// Quit when all windows are closed.
app.on('window-all-closed', function() {
  // If all windows are dead, must handle menus from main thread (this thread)
  Menu.setApplicationMenu(MainMenu); 
});

// require(__dirname + '/main-thread-ipc.js');

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the javascript object is GCed.
var mainWindow = null;

function exit() {
  killBackend();
}

function killBackend() {
  backend.kill('SIGTERM');
  backend.dead = true;
}

function spinUntilReady(url, done) {
  var interval = 100;
  var dur = 10; // In seconds
  var timeout = dur * (1000 / interval);
  console.log("note: probing until backend is ready, an error here is normal");
  var spin = function() {
    var callback = function(response) {
      if (response.statusCode == 200){
        done();
      } else {
        if (timeout <= 0) {
          console.log('Application did not start correctly. Please try relaunching it.');
          app.quit();
        } else {
          timeout--;
          setTimeout(spin, interval);
        }
      }
    }
    http.get(serverUrl + url, callback);
  }
  spin();
}

// This method will be called when Electron has done everything
// initialization and ready for creating browser windows.
app.on('ready', function() {
  // Run beaker backend
  runBeaker();

  eventEmitter.on('backendReady', function() {
    // Have to wait until actually ready
    var allReady = function(){
      // Create the browser window.
      mainWindow = new BrowserWindow(windowOptions.defaultWindowOptions);
      
      if (openFile !== undefined) {
        mainWindow.loadUrl(serverUrl + '/beaker/#/open?uri=' + openFile);
      } else {
        mainWindow.loadUrl(serverUrl);
      }

      MainMenu = Menu.buildFromTemplate(makeMenuTemplate(serverUrl));

      // Open the devtools.
      mainWindow.openDevTools();
      
      // Emitted when the window is closed.
      mainWindow.on('closed', function() {
        // Dereference the window object, usually you would store windows
        // in an array if your app supports multi windows, this is the time
        // when you should delete the corresponding element.
        mainWindow = null;
      });
      appReady = true;
    };
    spinUntilReady(backend.hash + '/beaker/rest/util/ready', allReady);
  });
});

// Have to make sure this waits until backend launches
app.on('open-file', function(event, path) {
  event.preventDefault();
  if (appReady){
    var newWindow = new BrowserWindow(windowOptions.defaultWindowOptions);
    newWindow.loadUrl(serverUrl + '/beaker/#/open?uri=' + path);
  } else {
    openFile = path;
  }
});

function runBeaker() {
  var ReadLine = require('readline');
  var spawn = require('child_process').spawn;
  process.env['JAVA_HOME'] = java_home;
  backend = spawn(path.resolve(__dirname + '/../dist/beaker.command'), ['--open-browser', 'false']);

  var rl = ReadLine.createInterface({
    input: backend.stdout
  });

  rl.on('line', function(line) {
    console.log(line); // Pipe backend's stdout to electron's stdout
    if (line.startsWith('Beaker hash')){
      backend.hash = line.split(' ')[2];
    }
    else if (line.startsWith('Beaker listening on')){
      serverUrl = line.split(' ')[3];
      eventEmitter.emit('backendReady', {});
    }
  });
}