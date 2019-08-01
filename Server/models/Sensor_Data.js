var mongoose = require('mongoose');
// mongoose.connect("mongodb://localhost:27017/mobileApp"); //'mongodb://127.0.0.1:27017/Chat'
mongoose.Promise = global.Promise;
var Schema = mongoose.Schema;

var sensorDataSchema = new Schema({
  _id:Number,
  name:String,
  light: Number,
  noise: Number,
  temp:Number,
  time_update:Number,
  lat:Number,
  long:Number,
}, {collection: 'sensor_data'});

module.exports =mongoose.model('sensor_data', sensorDataSchema);

