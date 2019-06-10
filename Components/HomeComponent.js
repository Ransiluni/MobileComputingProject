import React, {Component} from 'react';
import {Platform, StyleSheet, Text, Button, View,FlatList,TouchableOpacity} from 'react-native';
import { SearchBar } from 'react-native-elements';
import Navigator from '../Navigation/StackNavigation';



// type Props = {};
export default class HomeComponent extends React.Component {
  state = {
    search: '',
    title:'Search',
  };

  updateSearch = search => {
    this.setState({ search });
    
  };
 
  render() {
    const { search,title } = this.state;
    //const {navigate} = this.props.navigation;
    return (      
      <View style={styles.container}>
      <SearchBar
        containerStyle={{width:'100%',backgroundColor:'#0064ff'}}
        placeholder="Type Here..."
        onChangeText={this.updateSearch}
        value={search}
      />
        <FlatList
        style={styles.list}   
        data={[{key: 'Hilton Hotel'}, {key: 'Galadari Hotel'}]}
        renderItem={({item}) => <View>
        <TouchableOpacity 
        style={styles.restaurants}
        onPress={() => this.props.navigation.navigate('Profile')}
        >
        <Text style={styles.title}>{item.key}</Text>
        </TouchableOpacity></View>}/>
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
  list:{
    flex:1,
    backgroundColor:'#333333',
    width:'100%',
  },
  restaurants:{
    backgroundColor:'#ffffff',
    height:30,
    marginTop:2,
    borderRadius: 4,
    borderWidth: 0.5,
    borderColor: '#d6d7da',
    height:40
  },
  title:{
    fontSize:25,
    marginLeft:10
    
  }
});
