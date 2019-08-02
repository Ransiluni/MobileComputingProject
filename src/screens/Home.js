import React from 'react';
import { Platform, StyleSheet, Text, Button, View, FlatList, TouchableOpacity } from 'react-native';
import { SearchBar } from 'react-native-elements';
import { GooglePlacesAutocomplete } from 'react-native-google-places-autocomplete';

class Home extends React.Component {
  static navigationOptions = {
   header:null,
  };
  state = {
    search: '',
    title: 'Search',
    restaurants: []
  };

  updateSearch = search => {
    this.setState({ search });

  };

  componentDidMount() {
    fetch("https://mobilecomputingproject.herokuapp.com/place/list")
      .then(response => response.json())
      .then((responseJson) => {
        console.log(responseJson)
        this.setState({
          loading: false,
          restaurants: responseJson.items
        })
      })
      .catch(error => console.log(error)) //to catch the errors if any
  }

  render() {
    const { search, title } = this.state;
    //const {navigate} = this.props.navigation;
    return (
      <View style={styles.container}>
        <GooglePlacesAutocomplete
          placeholder='Search'
          minLength={2} // minimum length of text to search
          autoFocus={false}
          returnKeyType={'search'} // Can be left out for default return key https://facebook.github.io/react-native/docs/textinput.html#returnkeytype
          listViewDisplayed='false'    // true/false/undefined
          fetchDetails={true}
          renderDescription={row => row.description} // custom description render
          onPress={(data, details = null) => { // 'details' is provided when fetchDetails = true
            console.log(details.geometry.location.lat, details.geometry.location.lng);
            fetch("https://mobilecomputingproject.herokuapp.com/place/provide/"+details.geometry.location.lat+"/"+details.geometry.location.lng)
              .then(response => response.json())
              .then((responseJson) => {
                console.log(responseJson)
                this.setState({
                  loading: false,
                  restaurants: responseJson.results
                })
              })
              .catch(error => console.log(error)) //to catch the errors if any
          }
          }

          getDefaultValue={() => ''}

          query={{
            // available options: https://developers.google.com/places/web-service/autocomplete
            key: 'AIzaSyDDR1zoqTcEO0ks7wjfoGWVxiENQiFHX0g',
            language: 'en', // language of the results
            types: '(cities)', // default: 'geocode'
            // componentRestrictions: {country: "lk"}
          }}

          styles={{
            container:{
              height:30,
              zIndex:90,
              width:'100%',
              flex:1,
              left:0,
              top:0,
            },
            textInputContainer: {
              width: '100%'
            },
            listView:{
              height:30,
              backgroundColor:'black',
              color:'white'
            },
            description: {
              fontWeight: 'bold',
              color:'white'
            },
            predefinedPlacesDescription: {
              color: '#1faadb'
            }
          }}

          currentLocation={true} // Will add a 'Current location' button at the top of the predefined places list
          currentLocationLabel="Current location"
          nearbyPlacesAPI='GooglePlacesSearch' // Which API to use: GoogleReverseGeocoding or GooglePlacesSearch
          GoogleReverseGeocodingQuery={{
            // available options for GoogleReverseGeocoding API : https://developers.google.com/maps/documentation/geocoding/intro
          }}
          GooglePlacesSearchQuery={{
            // available options for GooglePlacesSearch API : https://developers.google.com/places/web-service/search
            rankby: 'distance',
            types: 'food'
          }}

          filterReverseGeocodingByTypes={['locality', 'administrative_area_level_3']} // filter the reverse geocoding results by types - ['locality', 'administrative_area_level_3'] if you want to display only cities
          //predefinedPlaces={[homePlace, workPlace]}

          debounce={200} // debounce the requests in ms. Set to 0 to remove debounce. By default 0ms.
        //renderLeftButton={() => <Image source={require('path/custom/left-icon')} />}
        //renderRightButton={() => <Text>Custom text after the input</Text>}
        />


        <FlatList
          style={styles.list}
          data={this.state.restaurants}
          renderItem={({ item }) => <View>
            <TouchableOpacity
              style={styles.restaurants}
              onPress={() => this.props.navigation.navigate('Profile', { "lat": item.lat, "long": item.long })}
            >
              <Text style={styles.title}>{item.name}</Text>
              <View style={{flexDirection: 'row'}}>
              <Text style={styles.title1}>Light-Level : {item.light>500?"Pleasant":"Low-Light"}</Text>
              <Text style={styles.title2}>Noise : {item.noise>100?"Loud":item.noise>70?"Busy":item.noise>40?"Calm":"Quiet" } ({Number(item.noise).toFixed(2)}dB)</Text>
              </View>
             


            </TouchableOpacity>
            </View>} />
       
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  list: {
    flex: 1,
    top:50,
    position:"absolute",
    backgroundColor: '#333333',
    width: '100%',
  },
  restaurants: {
    backgroundColor: '#ffffff',
    height: 50,
    marginTop: 2,
    borderRadius: 4,
    borderWidth: 0.5,
    borderColor: '#d6d7da',
  },
  title: {
    fontWeight: 'bold',
    fontSize: 20,
    marginLeft: 20,
    marginBottom:0

  },
  title1: {
    fontSize: 15,
    marginLeft: 15,
    color:"red"

  },
  title2: {
    fontSize: 15,
    marginLeft: 20,
    color:"blue"

  }
});
export default Home;