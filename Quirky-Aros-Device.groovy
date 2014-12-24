/**
 *  Quirky-Aros
 *
 *  Copyright 2014 Todd Wackford
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *****************************************************************
 *                       Changes
 *****************************************************************
 *
 *  Change 1:	2014-05-19 (wackford)
 *				Initial Build
 *
 *  Change 2:	2014-5-20 (twackford)
 *				Added functionality to have mode auto_eco upon up/down setpoint button press
 *
 *  Change 3:	2014-6-5 (twackford)
 *				Multiple bug fixes after initial QA
 *
 *
 *****************************************************************
 *                       Code
 *****************************************************************
 */

metadata {
	// Automatically generated. Make future change here.
	definition (name: "Quirky Aros", namespace: "wackford", author: "todd@wackford.net", oauth: true) {
        capability "Switch"
        capability "Temperature Measurement"
		capability "Refresh"
		capability "Thermostat"
		capability "Configuration"
		capability "Polling"

		attribute "coolingSetpoint", "string"
        attribute "mode", "string"
        attribute "fanMode", "string"

		command "coolLevelUp"
		command "coolLevelDown"
		command "switchMode"
		command "switchFanMode"
        command "auto_eco"
        command "fan_only"
        command "cool_only"
        command "fanLow"
        command "fanMed"
        command "fanHigh"
	}

	// simulator metadata
	simulator {
	}

	tiles {
		valueTile("temperature", "device.temperature", width: 2, height: 2) {
			state("temperature", label:'${currentValue}°', unit:'',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "off", label: "Aros Off", action: "switch.on", icon: "st.custom.quirky.quirky-device", backgroundColor: "#ffffff"
            state "on", label: "Aros On", action: "switch.off", icon: "st.custom.quirky.quirky-device", backgroundColor: "#79b821"	
		}
		standardTile("mode", "device.mode", inactiveLabel: false, decoration: "flat") {
			state "cool_only", label:"Cool", action:"switchMode", icon:"st.Weather.weather7"
            state "auto_eco", label:"Eco", action:"switchMode", icon:"st.Weather.weather7"
			state "fan_only", label:"Fan", action:"switchMode", icon:"st.Weather.weather7"
		}
		standardTile("fanMode", "device.fanMode", inactiveLabel: false, decoration: "flat") {
			state "fanLow", label:"Low", action:"switchFanMode", icon:"st.Appliances.appliances11"
			state "fanMed", label:"Med", action:"switchFanMode", icon:"st.Appliances.appliances11"
			state "fanHigh", label:"High", action:"switchFanMode", icon:"st.Appliances.appliances11"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false, decoration: "flat") {
			state "coolingSetpoint", label:'${currentValue}° Set', unit:"", backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("coolLevelUp", "device.heatingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "coolLevelUp", action:"coolLevelUp", backgroundColor:"#d04e00", icon:"st.thermostat.thermostat-up"
        }
        standardTile("coolLevelDown", "device.heatingSetpoint", canChangeIcon: false, inactiveLabel: false, decoration: "flat") {
            state "coolLevelDown", action:"coolLevelDown", backgroundColor: "#1e9cbb", icon:"st.thermostat.thermostat-down"
        }
        
		main "temperature"
		details(["temperature", "mode", "fanMode", "coolLevelDown", "coolingSetpoint", "coolLevelUp", "switch", "refresh"])
	}
}

def parse(description) {
	log.debug "parse() - $description"
	def results = []
    
    if ( description == "updated" )
    	return //nothing here interesting

	if (description?.name && description?.value)
	{
		results << sendEvent(name: "${description?.name}", value: "${description?.value}")
	}
}



def coolLevelUp(){
	if ( device.currentValue("switch") == "off" ) {
    	log.debug "user tapped a tile while aros was off, we'll turn it on for them"
    	delayBetween([
        	on(),
        	switchToMode("auto_eco")
        ])
    }
    
    if ( device.currentValue("mode") == "fan_only" ) { // wink doesn;'t let us change this in fan_only mode
    	log.debug "we're in fan_ony mode, ignoring coolingSetPoint adjustment"
    	return
    }
        
	def locationScale = getTemperatureScale()
    def nextLevel = null
    
    if ( locationScale == "F" ) {
    	state.setPoint = state.setPoint ?: 64 // this is kinda wierd the first time after install. then ok
    	nextLevel = state.setPoint + 1
        if( nextLevel > 86){
    		nextLevel = 86 		//Aros max value in F for set point
    	}
    } else {
    	state.setPoint = state.setPoint ?: 17 // this is kinda wierd the first time after install. then ok
    	nextLevel = state.setPoint + 1
        if( nextLevel > 30){
    		nextLevel = 30 		//Aros max value in C for set point
    	}   
    }

    state.setPoint = nextLevel
    
    log.debug "Setting cool set point up to: ${nextLevel}"
    coolingSetpoint(nextLevel)
}

def coolLevelDown(){
	if ( device.currentValue("switch") == "off" ) {
    	log.debug "user tapped a tile while aros was off, we'll turn it on for them"
    	delayBetween([
        	on(), // user tapped a tile while aros was off, we'll turn iton for them
        	switchToMode("auto_eco")
        ])
    }
    
    if ( device.currentValue("mode") == "fan_only" ) { // wink doesn;'t let us change this in fan_only mode
    	log.debug "we're in fan_ony mode, ignoring coolingSetPoint adjustment"
        return
    }    
     
    def locationScale = getTemperatureScale()
    def nextLevel = null
    
    if ( locationScale == "F" ) {
    	state.setPoint = state.setPoint ?: 86 // this is kinda wierd the first time after install. then ok
    	nextLevel = state.setPoint - 1
        if( nextLevel < 64){
    		nextLevel = 64 		//Aros min value in F for set point
    	}
    } else {
    	state.setPoint = state.setPoint ?: 30 // this is kinda wierd the first time after install. then ok
    	nextLevel = state.setPoint - 1
        if( nextLevel < 17){
    		nextLevel = 17 		//Aros min value in C for set point
    	}   
    }

    state.setPoint = nextLevel
    
    log.debug "Setting cool set point down to: ${nextLevel}"
    coolingSetpoint(nextLevel)
}

def coolingSetpoint(degrees) {
	log.debug "in method coolingSetpoint with degrees = ${degrees}"
	def locationScale = getTemperatureScale()
    degrees = degrees as double
    def deviceValue = degrees
    
    if (locationScale == "F")
    	deviceValue = fahrenheitToCelsius(degrees)
    
    degrees = degrees.trunc(2)
    
	sendEvent(name: coolingSetpoint, value: degrees)
	parent.arosCoolingSetpoint(this, ["max_set_point": deviceValue])
}

def setCoolingSetpoint(degrees) {
	coolingSetpoint(degrees)
}

def setHeatingSetpoint(degrees) {
	log.debug "The Aros has no heater, but we have a dummy method to act like a thermostat"
}

def modes() {
	["cool_only", "auto_eco", "fan_only"]
}

def fanModes() {
	["fanLow", "fanMed", "fanHigh"]
}

def switchMode() {
	if ( device.currentValue("switch") == "off" )
    	on() // user tapped a tile while aros was off, we'll turn iton for them
    
	def currentMode = device.currentState("thermostatMode")?.value
	def lastTriedMode = getDataByName("lastTriedMode") ?: currentMode ?: "off"
	def supportedModes = getDataByName("supportedModes")
	def modeOrder = modes()
	def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
	def nextMode = next(lastTriedMode)
	if (supportedModes?.contains(currentMode)) {
		while (!supportedModes.contains(nextMode) && nextMode != "off") {
			nextMode = next(nextMode)
		}
	}
    log.debug "Switching to mode: ${nextMode}"
	switchToMode(nextMode)
}

def switchToMode(nextMode) {
	def supportedModes = getDataByName("supportedModes")
	if(supportedModes && !supportedModes.contains(nextMode)) log.warn "thermostat mode '$nextMode' is not supported"
	if (nextMode in modes()) {
		updateState("lastTriedMode", nextMode)
		return "$nextMode"()
	} else {
		log.debug("no mode method '$nextMode'")
	}
}

def switchFanMode() {
	if ( device.currentValue("switch") == "off" ) {
    	log.debug "user tapped a tile while aros was off, we'll turn it on for them"
    	on()
    } 
	def currentMode = device.currentState("thermostatFanMode")?.value
	def lastTriedMode = getDataByName("lastTriedFanMode") ?: currentMode ?: "off"
	def supportedModes = getDataByName("supportedFanModes") ?: "fanLow fanMed fanHigh"
	def modeOrder = fanModes()
	def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
	def nextMode = next(lastTriedMode)
	while (!supportedModes?.contains(nextMode) && nextMode != "fanAuto") {
		nextMode = next(nextMode)
	}
	switchToFanMode(nextMode)
}

def switchToFanMode(nextMode) {
	def supportedFanModes = getDataByName("supportedFanModes")
	if(supportedFanModes && !supportedFanModes.contains(nextMode)) log.warn "thermostat mode '$nextMode' is not supported"

	if (nextMode in fanModes()) {
		updateState("lastTriedFanMode", nextMode)
		return "$nextMode"()
	} else {
		log.debug("no fanMode method '$nextMode'")
	}
}

def updateState(String name, String value) {
	state[name] = value
	device.updateDataValue(name, value)
}

def getDataByName(String name) {
	state[name] ?: device.getDataValue(name)
}

def fanLow() {
	
	log.debug "Executing ${[fan_speed: "Low"]}"
	delayBetween([
    	parent.arosFanSpeed(this, [fan_speed: 0.333]),
    	sendEvent(name: "fanMode", value: "fanLow")
    ])
}

def fanMed() {
	log.debug "Executing ${[fan_speed: "Med"]}"
	delayBetween([
    	parent.arosFanSpeed(this, [fan_speed: 0.666]),
    	sendEvent(name: "fanMode", value: "fanMed")
    ])
}

def fanHigh() {
	log.debug "Executing ${[fan_speed: "High"]}"
	delayBetween([
    	parent.arosFanSpeed(this, [fan_speed: 0.999]),
    	sendEvent(name: "fanMode", value: "fanHigh")
    ])
}

def cool_only() {
	log.debug "Executing ${[mode: cool_only]}"
    delayBetween([
    	parent.arosMode(this, "cool_only"),
    	sendEvent(name: "mode", value: "cool_only")
    ])
	
}

def auto_eco() {
	log.debug "Executing ${[mode: auto_eco]}"
    delayBetween([
    	parent.arosMode(this, "auto_eco"),
    	sendEvent(name: "mode", value: "auto_eco")
	])
}

def fan_only() {
	log.debug "Executing ${[mode: fan_only]}"
    delayBetween([
    	parent.arosMode(this, "fan_only"),
    	sendEvent(name: "mode", value: "fan_only")
	])
}

def on() {
	log.debug "Executing 'on'"
    //log.debug this
	delayBetween([
    	parent.arosOn(this),
    	sendEvent(name: "switch", value: "on")
    ])
}

def off() {
	log.debug "Executing 'off'"
	delayBetween([
    	parent.arosOff(this),
    	sendEvent(name: "switch", value: "off")
    ])
}

def uninstalled() {
	log.debug "Executing 'uninstall' in child"
    parent.uninstallChildDevice(this)
}

def poll() {
	log.debug "Executing 'poll'"
	parent.pollAros(this)
}

def refresh() {
	log.debug "Executing 'refresh'"
	parent.pollAros(this)
}

