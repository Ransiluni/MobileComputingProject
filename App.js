import React, {Component} from 'react';
import {Platform, StyleSheet, Text, Button, View,FlatList,TouchableOpacity} from 'react-native';
import { SearchBar } from 'react-native-elements';
import HomeComponent from './Components/HomeComponent';
import Navigator from './Navigation/StackNavigation'
import MapComponent from './Components/MapComponent';



// type Props = {};
// export default class App extends Component<Props> {

//   render() {
//     return (
//       <View style={styles.container}>
//      <HomeComponent />
//       </View>
//     );
//   }
// }

// const styles = StyleSheet.create({
//   container: {
//     flex: 1,
//   },
// });
const App = () => <Navigator/>
export default App;