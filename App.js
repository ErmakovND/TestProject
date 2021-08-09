/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React from "react";
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  Button,
  NativeEventEmitter,
  PermissionsAndroid,
} from "react-native";

import { Header, Colors } from "react-native/Libraries/NewAppScreen";

import StepDetector from "./src/StepDetector";
import FileSystem from "./src/FileSystem";
import Audio from "./src/Audio";

const addStep = (state) => {
  return { steps: state.steps + 1 };
};

const stepEventEmitter = new NativeEventEmitter(StepDetector);

const App = () => {
  const [file, setFile] = React.useState("");
  const [state, dispatch] = React.useReducer(addStep, { steps: 0 });

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>File</Text>
              <Text style={styles.sectionDescription}>{file}</Text>
              <Button
                title="Get Permission"
                onPress={() => {
                  PermissionsAndroid.request(
                    PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
                    {
                      title: "App Permission",
                      message: "App asks to read storage",
                      buttonNeutral: "Later",
                      buttonNegative: "No",
                      buttonPositive: "Ok",
                    },
                  )
                    .then(granted => {
                      alert("Permission granted: " + granted);
                    })
                    .catch(err => {
                      console.warn(err);
                    });
                }}
              />
              <Button
                title="Get Audio"
                onPress={() => {
                  FileSystem.getAudioFiles()
                    .then(data => {
                      setFile(data[1].data);
                      console.log(data);
                    })
                    .catch(error => {
                      console.log(error);
                    });
                }}
              />
              <Button
                title="Get BPM"
                onPress={() => {
                  Audio.getBPM(file)
                    .then(bpm => {
                      alert(bpm);
                    })
                    .catch(err => {
                      alert(err);
                    });
                }}
              />
            </View>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: "absolute",
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: "600",
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: "400",
    color: Colors.dark,
  },
  highlight: {
    fontWeight: "700",
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: "600",
    padding: 4,
    paddingRight: 12,
    textAlign: "right",
  },
});

export default App;
