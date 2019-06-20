var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
mongoose.connect("mongodb://localhost:27017/mobileApp"); //'mongodb://127.0.0.1:27017/Chat'
mongoose.Promise = global.Promise;
var Schema = mongoose.Schema;

var placeDataSchema = new Schema({
  _id:Number,
  place_name: String,
  lat: String,
  long:String,
  type:String
}, {collection: 'place'});

var PlaceData = mongoose.model('place', placeDataSchema);

//pass place id when lat long given
router.get('/provide/:lat/:long', function(req, res,next) {
  var lat_c=req.params.lat; 
  var long_c=req.params.long; 
  console.log(lat_c,long_c);
  //let query ={'lat':lat_c}
  PlaceData.find({$and:[{'lat':lat_c},{'long':long_c}]},{ _id: 0, place_name: 1})
  .then(function(items) {
    res.send({'results': items});
    console.log(items);
  })
  .catch(function(err) {
    console.log(err);
  }); 
});

//send all place names with lat and long 
router.get('/list', function(req, res,next) {
  PlaceData.find({},{_id:0,lat:1,long:1})
  .then(function(items) {
    res.send({items});
    console.log(items);
  })
  .catch(function(err) {
    console.log(err);
  }); 
});

module.exports = router;