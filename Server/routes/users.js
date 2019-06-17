var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
//mongoose.connect(process.env.MONGOLAB_URI); //'mongodb://127.0.0.1:27017/Chat'
mongoose.Promise = global.Promise;
var Schema = mongoose.Schema;

var userDataSchema = new Schema({
  first:String,
  last: String,
  email: String,
  thumbnail:String,
  password:String
}, {collection: 'users'});

var UserData = mongoose.model('users', userDataSchema);

//generate a list of all contacts except the logged in user
router.get('/list/:user', function(req, res,next) {
  var user=req.params.user; 
  console.log(user);
  UserData.find({ 'email':{$ne : user} })
  .then(function(items) {
    res.send({'results': items});
  })
  .catch(function(err) {
    console.log(err);
  }); 
});

//check for login credentials
router.post('/login', function(req, res, next) {
  
  var username=req.body.username;
  var password=req.body.password;

  console.log(username)
  res.send(password)

  // UserData.find({ email: username, password: password})
  // .then(function(items) {
  //   if(items.length===1){
  //     res.send({
  //       'success':true,
  //       'user':username,
  //   });
  //   }
  //   else{
  //     res.send({
  //       'message':'Wrong login credentials'
  //     });
  //   } 
  // })
  // .catch(function(err) {
  //   console.log(err);
  // });  
});

module.exports = router;//
