import React from 'react';
import { Dimensions, StyleSheet } from 'react-native';
import MapView from 'react-native-maps';
import MapViewDirections from 'react-native-maps-directions';

const origin = {latitude: 7.0864, longitude: 80.0335};
const destination = {latitude: 6.9271, longitude: 79.8612};
const GOOGLE_MAPS_APIKEY = 'AIzaSyDDR1zoqTcEO0ks7wjfoGWVxiENQiFHX0g';

const { width, height } = Dimensions.get('window');
const ASPECT_RATIO = width / height;
const LATITUDE = 7.8731;
const LONGITUDE = 80.7718;
const LATITUDE_DELTA = 0.0922;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;

class Profile extends React.Component {
  static navigationOptions = {
    title: 'Profile'
   };
 render() {
 return (
 <MapView  style={{flex:1}}
 initialRegion={{
  latitude: LATITUDE,
  longitude: LONGITUDE,
  latitudeDelta: LATITUDE_DELTA,
  longitudeDelta: LONGITUDE_DELTA,
}}>
  <MapViewDirections
  style={{flex:1}}
    origin={origin}
    destination={destination}
    apikey={GOOGLE_MAPS_APIKEY}
    strokeWidth={6}
    mode="DRIVING"
    strokeColor="hotpink"
  />
</MapView>
);
}
}
export default Profile;