var express = require('express');
var router = express.Router();

var mongoose = require('mongoose');
// try {
//     mongoose.connect("mongodb://localhost:27017/mobileApp"); //- starting a db connection
// }catch(err) {
//   console.log(err)
//     //mongoose.createConnection("mongodb://localhost:27017/mobileApp"); //- starting another db connection
// }
//mongoose.connect("mongodb://localhost:27017/mobileApp"); //'mongodb://127.0.0.1:27017/Chat'
mongoose.Promise = global.Promise;
var Schema = mongoose.Schema;

var sensorDataSchema = new Schema({
  _id:Number,
  light: Number,
  noise: Number,
  temp:Number,
  time_update:Number,
  lat:Number,
  long:Number,
}, {collection: 'sensor_data'});

var SensorData = mongoose.model('sensor_data', sensorDataSchema);


var light_array=[];
var temp_array=[];
var noise_array=[];
var current_Time=new Date().getTime();

//sensor data append
router.post('/set', function(req, res,next) {
    console.log(req.body)
    var lat=req.body.lat; 
    var long=req.body.long; 
    var light=req.body.light_data; 
    var noise=req.body.noise_data;
    var temp=req.body.tem_data; 
    light_array.push(light);
    temp_array.push(temp);
    noise_array.push(noise)
    console.log(current_Time)
    console.log(new Date().getTime())
    time_dif=(new Date().getTime())-(current_Time)
    console.log(time_dif)
    if(time_dif > 5000){
        console.log(light_array)
        const avg_light =light_array.reduce((a,b) => a + b, 0) / light_array.length
        const avg_temp =temp_array.reduce((a,b) => a + b, 0) / temp_array.length
        const avg_noise =noise_array.reduce((a,b) => a + b, 0) / noise_array.length
        console.log(avg_light)
        current_Time=new Date().getTime();

        console.log(current_Time)

        var myquery = {  $and: [ {'lat':Number(lat)} , { 'long':Number(long) } ]};
        var newvalues = {$set: { "light": avg_light, "temp":avg_temp,"noise":avg_noise,"time_update":current_Time }};
        
        
        mongoose.connect("mongodb://localhost:27017/mobileApp",function(err, db) { //- starting a db connection
        SensorData.update(myquery, newvalues).then( function(err, res,stat) {
        if (err) {console.log(err)};
        if (res){console.log(res)}
        console.log(stat)
        console.log("1 document updated");
        });
        SensorData.find({}).then(function(items){
          console.log(items)
        })
        console.log("update_done")
        db.close();
    });

    
    console.log(light);
  }
  res.send('Success')
  
});

  

module.exports = router;
