var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
//mongoose.connect(process.env.MONGOLAB_URI)
//mongoose.Promise = global.Promise;
var Schema = mongoose.Schema;

var messageDataSchema = new Schema({
    message:String,
    sender:String,
    receiver:String,
    time:String,
    date:String
}, {collection: 'chat'});

var MessageData = mongoose.model('MessageData', messageDataSchema);

//save sent messages in the database
router.post('/send', function (req, res, next) {
    
    var message = req.body.message;
    var sender = req.body.sender;
    var receiver = req.body.receiver;
    var time = req.body.time;
    var date = req.body.date;
    console.log(receiver);
    

    console.log(message, sender, receiver, time, date);
    var item= { 
        message: message, 
        sender: sender, 
        receiver: receiver,
        time: time, 
        date: date
     }
    req.app.io.emit(receiver,item);
    var data = new MessageData(item);
    data.save()
    .catch(function(err) {
        console.log(err);
        }); 
    res.send({ 'message': 'message sent' });
});


//update message list
router.post('/receive', function (req, res, next) {
    
    var sender = req.body.sender;
    var receiver = req.body.receiver;
    console.log(sender, receiver);

    MessageData.find({ $or: [{ sender: sender, receiver: receiver }, { sender: receiver, receiver: sender }] }).sort({ "date": 1 })
    .then(function(items) {
    res.send({'data': items});
    })
    .catch(function(err) {
    console.log(err);
    }); 
});

module.exports = router;
