var http = require('http'),
    path = require('path'),
    methods = require('methods'),

    bodyParser = require('body-parser'),
    session = require('express-session'),
    cors = require('cors'),
    passport = require('passport'),
    errorhandler = require('errorhandler');

    var createError = require('http-errors');
    var express = require('express');
    var path = require('path');
    var cookieParser = require('cookie-parser');
    var logger = require('morgan');
    
    
    
    var indexRouter = require('./routes/index');
    var usersRouter = require('./routes/place');
    var dataRouter = require('./routes/sensor_data');
    // var messagesRouter = require('./routes/messages');
    
    var app = express();
    
    
    // view engine setup
    app.set('views', path.join(__dirname, 'views'));
    app.set('view engine', 'jade');
    
    app.use(logger('dev'));
    app.use(bodyParser.json());
    app.use(bodyParser.urlencoded({ extended: false }));
    app.use(cookieParser());
    app.use(express.static(path.join(__dirname, 'public')));
    
    app.use('/', indexRouter);
    app.use('/place', usersRouter);
    app.use('/data', dataRouter);
    
    // catch 404 and forward to error handler
    app.use(function(req, res, next) {
      next(createError(404));
    });
    
    // error handler
    app.use(function(err, req, res, next) {
      // set locals, only providing error in development
      res.locals.message = err.message;
      res.locals.error = req.app.get('env') === 'development' ? err : {};
    
      // render the error page
      res.status(err.status || 500);
      res.render('error');
    });
    
    var server= require('http').createServer(app);
    //var io=require('socket.io')(server);
    
    var port = process.env.PORT;
    console.log('listening on '+port);
    // app.io = io;
    // io.on('connection', function (socket) {
    //   console.log('connected'+socket.id)
    //   socket.on('send',data=>{
    //     console.log(data)
    //   });
    // });
    server.listen(port);
    
    
    module.exports = app;