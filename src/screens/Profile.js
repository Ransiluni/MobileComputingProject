import React from 'react';
import { Dimensions, StyleSheet,View,Button } from 'react-native';
import MapView from 'react-native-maps';
import MapViewDirections from 'react-native-maps-directions';
import GetLocation from 'react-native-get-location'
import getDirections from 'react-native-google-maps-directions'

const origin = { latitude: 7.0864, longitude: 80.0335 };
const destination = { latitude: 6.9271, longitude: 79.8612 };
const GOOGLE_MAPS_APIKEY = 'AIzaSyDDR1zoqTcEO0ks7wjfoGWVxiENQiFHX0g';

const { width, height } = Dimensions.get('window');
const ASPECT_RATIO = width / height;
const LATITUDE = 7.8731;
const LONGITUDE = 80.7718;
const LATITUDE_DELTA = 0.0922;
const LONGITUDE_DELTA = LATITUDE_DELTA * ASPECT_RATIO;



class Profile extends React.Component {
  static navigationOptions = {
    title: 'Navigation'
  };
  state = {
    location: '',
  };

  handleGetDirections = () => {
    const data = {
       source: {
        latitude: this.state.location.latitude,
        longitude: this.state.location.longitude
      },
      destination: {
        latitude: this.props.navigation.state.params.lat,
        longitude: this.props.navigation.state.params.long
      },
      params: [
        {
          key: "travelmode",
          value: "driving"        // may be "walking", "bicycling" or "transit" as well
        },
        {
          key: "dir_action",
          value: "navigate"       // this instantly initializes navigation using the given travel mode
        }
      ],
    }
 
    getDirections(data)
  }

  componentDidMount() {

    GetLocation.getCurrentPosition({
      enableHighAccuracy: true,
      timeout: 15000,
    })
      .then(location => {
        this.setState({
          location:location
        })
        console.log(location);
      })
      .catch(error => {
        const { code, message } = error;
        console.warn(code, message);
      })
  }

  render() {
    return (
      <View style={{ flex: 1 }}>
      <MapView style={{ flex: 1 }}
        initialRegion={{
          latitude: this.props.navigation.state.params.lat,
          longitude: this.props.navigation.state.params.long,
          latitudeDelta: LATITUDE_DELTA,
          longitudeDelta: LONGITUDE_DELTA,
        }}>
        <MapViewDirections
          style={{ flex: 1 }}
          origin={{ latitude: this.state.location.latitude, longitude:this.state.location.longitude }}
          destination={{ latitude: this.props.navigation.state.params.lat, longitude: this.props.navigation.state.params.long }}
          apikey={GOOGLE_MAPS_APIKEY}
          strokeWidth={6}
          mode="DRIVING"
          strokeColor="hotpink"
        />
      </MapView>
       <Button onPress={this.handleGetDirections} title="Get Directions" />
     </View>
    );
  }
}
export default Profile;