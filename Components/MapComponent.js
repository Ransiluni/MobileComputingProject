import React, {Component} from 'react';
import {Platform, StyleSheet, Text, Button, View,FlatList,TouchableOpacity} from 'react-native';
import getDirections from 'react-native-google-maps-directions'



// type Props = {};
export default class MapComponent extends React.Component{
  state = {
    search: '',
    title:'Search',
  };
  static navigationOptions = {
    title: 'Welcome',
  };

  handleGetDirections = () => {
    const data = {
       source: {
        latitude: -33.8356372,
        longitude: 18.6947617
      },
      destination: {
        latitude: -33.8600024,
        longitude: 18.697459
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
      ]
    
    }
 
    getDirections(data)
  }
 
  render() {
    //const {navigate} = this.props.navigation;
    return (
      <View style={styles.container}>
        <Button title="Go to Home screen" onPress={() => this.props.navigation.navigate('Home')} />
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
});
