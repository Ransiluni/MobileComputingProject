// import {createStackNavigator, createAppContainer,StackNavigator} from 'react-navigation';
// import Home from '../Components/HomeComponent';
// import map from '../Components/MapComponent';
import React, { Component } from "react";
import { createStackNavigator, createAppContainer } from 'react-navigation';


import {home} from '../Components/HomeComponent';
import map from '../Components/MapComponent';

const MainNavigator = createStackNavigator({
 
  Profile: {screen: map},
  Home: {screen: home},
});

export default createAppContainer(MainNavigator);




