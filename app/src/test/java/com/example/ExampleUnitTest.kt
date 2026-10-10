package com.example

import com.example.ai.NaturalCommandParser
import com.example.model.ActionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testYouTubeHindiCommand() {
        val intent = NaturalCommandParser.parse("YouTube par Honey Singh ka gaana chalao")
        assertEquals(ActionType.YOUTUBE_SEARCH, intent.actionType)
        assertTrue(intent.query?.contains("Honey Singh", ignoreCase = true) == true)
    }

    @Test
    fun testYouTubeBengaliCommand() {
        val intent = NaturalCommandParser.parse("YouTube e Arijit Singh er gaan bajao")
        assertEquals(ActionType.YOUTUBE_SEARCH, intent.actionType)
        assertTrue(intent.query?.contains("Arijit Singh", ignoreCase = true) == true)
    }

    @Test
    fun testOpenAppCommands() {
        val wp = NaturalCommandParser.parse("WhatsApp kholo")
        assertEquals(ActionType.OPEN_APP, wp.actionType)
        assertEquals("whatsapp", wp.appName)

        val ch = NaturalCommandParser.parse("Chrome kholo")
        assertEquals(ActionType.OPEN_APP, ch.actionType)
        assertEquals("chrome", ch.appName)

        val calc = NaturalCommandParser.parse("Calculator kholo")
        assertEquals(ActionType.OPEN_APP, calc.actionType)
        assertEquals("calculator", calc.appName)
    }

    @Test
    fun testAlarmCommand() {
        val intent = NaturalCommandParser.parse("Kal subah 6 baje alarm lagao")
        assertEquals(ActionType.SET_ALARM, intent.actionType)
        assertEquals(6, intent.hour)
        assertEquals(0, intent.minute)
    }

    @Test
    fun testFlashlightCommand() {
        val onIntent = NaturalCommandParser.parse("Flashlight on karo")
        assertEquals(ActionType.TOGGLE_FLASHLIGHT, onIntent.actionType)
        assertTrue(onIntent.turnOnFlashlight)

        val offIntent = NaturalCommandParser.parse("Flashlight off karo")
        assertEquals(ActionType.TOGGLE_FLASHLIGHT, offIntent.actionType)
        assertTrue(!offIntent.turnOnFlashlight)
    }

    @Test
    fun testStopListeningCommand() {
        val intent = NaturalCommandParser.parse("Stop listening")
        assertEquals(ActionType.STOP_LISTENING, intent.actionType)
    }
}
