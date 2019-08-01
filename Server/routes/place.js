var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');

mongoose.Promise = global.Promise;
var Schema = mongoose.Schema;

var SensorData = require("../models/Sensor_Data")

//pass place id when lat long given
router.get('/provide/:lat/:long', function(req, res,next) {
  var lat_c=req.params.lat; 
  var long_c=req.params.long; 

  //function to calculate the distance between two lat long points 
  function distance(lat1,lon1,lat2,lon2) {
    var R = 6371; // distance in kms
    var dLat = (lat2-lat1) * Math.PI / 180;
    var dLon = (lon2-lon1) * Math.PI / 180;
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(lat1 * Math.PI / 180 ) * Math.cos(lat2 * Math.PI / 180 ) *
      Math.sin(dLon/2) * Math.sin(dLon/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;
    return d;
  }

  mongoose.connect("mongodb://shyani:shyani123@ds157857.mlab.com:57857/sensor_data",function(err, db) {
    SensorData.find({})
    .then(function(items) {
      final=[]
      items.forEach(element => {
        if (distance(element.lat,element.long,lat_c,long_c)<0.1){
          final.push(element)
        }
      });
      res.send({'results': final});
      console.log(final);
    })
    .catch(function(err) {
      console.log(err);
    }); 
  });
});

//send all places with lat and long 
router.get('/list', function(req, res,next) {
  mongoose.connect("mongodb://shyani:shyani123@ds157857.mlab.com:57857/sensor_data",function(err, db) {
    SensorData.find({},{_id:0,lat:1,long:1})
    .then(function(items) {
      res.send({items});
    })
    .catch(function(err) {
      console.log(err);
    });
  }); 
});


module.exports = router;