import io
import sys
import unittest
from typing import cast
from unittest.mock import patch

from app.alerts.temperature_alert import TemperatureAlert
from app.alerts.wind_speed_alert import WindSpeedAlert
from app.observer_factory import ObserverRegistry, ObserverType
from app.weather_data import WeatherData
from app.weather_display import WeatherDisplay
from app.weather_generator import (
    CompositeWeatherGenerator,
    RandomWeatherGenerator,
    SimpleWeatherGenerator,
)
from app.weather_station import WeatherStation


class WeatherMonitoringIntegrationTests(unittest.TestCase):
    def setUp(self):
        self.weather_station = WeatherStation()

        self.registry = ObserverRegistry()

        self.captured_output = io.StringIO()
        self.stdout_backup = sys.stdout
        sys.stdout = self.captured_output

    def tearDown(self):
        sys.stdout = self.stdout_backup

    def test_observer_registration_and_notification(self):
        display = self.registry.create_observer(ObserverType.WEATHER_DISPLAY)
        temp_alert = self.registry.create_observer(ObserverType.TEMPERATURE_ALERT)

        self.weather_station.register_observer(display)
        self.weather_station.register_observer(temp_alert)

        self.captured_output.truncate(0)
        self.captured_output.seek(0)

        self.weather_station.set_measurements(32.5, 78.0, 15.0)

        output = self.captured_output.getvalue()

        self.assertIn("WeatherDisplay: Showing Temperature = 32.5°C", output)

        self.assertTrue(
            "TemperatureAlert: **Alert!" in output or
            "TemperatureAlert: No alert" in output
        )

    def test_observer_removal(self):
        display = self.registry.create_observer(ObserverType.WEATHER_DISPLAY)
        temp_alert = self.registry.create_observer(ObserverType.TEMPERATURE_ALERT)

        self.weather_station.register_observer(display)
        self.weather_station.register_observer(temp_alert)

        self.captured_output.truncate(0)
        self.captured_output.seek(0)

        self.weather_station.remove_observer(temp_alert)

        self.weather_station.set_measurements(33.0, 80.0, 16.0)

        output = self.captured_output.getvalue()

        self.assertIn("WeatherDisplay: Showing Temperature = 33.0°C", output)
        self.assertNotIn("TemperatureAlert", output)

    def test_multiple_alerts(self):
        temp_alert = self.registry.create_observer(ObserverType.TEMPERATURE_ALERT)
        humid_alert = self.registry.create_observer(ObserverType.HUMIDITY_ALERT)
        wind_alert = self.registry.create_observer(ObserverType.WIND_SPEED_ALERT)

        self.weather_station.register_observer(temp_alert)
        self.weather_station.register_observer(humid_alert)
        self.weather_station.register_observer(wind_alert)

        self.captured_output.truncate(0)
        self.captured_output.seek(0)

        self.weather_station.set_measurements(40.0, 95.0, 25.0)

        output = self.captured_output.getvalue()

        alert_count = 0
        if "TemperatureAlert: **Alert!" in output:
            alert_count += 1
        if "HumidityAlert: **Alert!" in output:
            alert_count += 1
        if "WindSpeedAlert: **Alert!" in output:
            alert_count += 1

        self.assertGreater(alert_count, 0)

    def test_weather_data_validation(self):
        try:
            WeatherData(temperature=30.0, humidity=70.0, wind_speed=15.0)
        except Exception as e:
            self.fail(f"WeatherData constructor raised exception with valid data: {e}")

        with self.assertRaises(TypeError):
            WeatherData(temperature="invalid", humidity=70.0, wind_speed=15.0)

    def test_weather_generators(self):
        initial_data = WeatherData(temperature=28.0, humidity=70.0, wind_speed=12.0)

        simple_gen = SimpleWeatherGenerator()
        random_gen = RandomWeatherGenerator()
        composite_gen = CompositeWeatherGenerator()

        simple_result = simple_gen.generate_weather(initial_data, 1)

        with patch('random.uniform', side_effect=[(1.0), (2.0), (3.0)]):
            random_result = random_gen.generate_weather(initial_data, 1)

        composite_result = composite_gen.generate_weather(initial_data, 1)

        self.assertEqual(simple_result.temperature, 30.0)  # 28.0 + 2.0
        self.assertEqual(simple_result.humidity, 72.0)  # 70.0 + 2.0
        self.assertEqual(simple_result.wind_speed, 15.0)  # 12.0 + 3.0

        self.assertNotEqual(random_result.temperature, simple_result.temperature)

        self.assertEqual(composite_result.temperature, simple_result.temperature)

        with patch('random.uniform', side_effect=[(1.0), (2.0), (3.0)]):
            week8_result = composite_gen.generate_weather(initial_data, 8)
            self.assertNotEqual(week8_result.temperature, simple_result.temperature)

    def test_observer_registry(self):
        display = self.registry.create_observer(ObserverType.WEATHER_DISPLAY)
        temp_alert = self.registry.create_observer(ObserverType.TEMPERATURE_ALERT)

        self.assertIsInstance(display, WeatherDisplay)
        self.assertIsInstance(temp_alert, TemperatureAlert)

        created_types = self.registry.get_created_types()
        self.assertIn(ObserverType.WEATHER_DISPLAY, created_types)
        self.assertIn(ObserverType.TEMPERATURE_ALERT, created_types)

        retrieved_display = self.registry.get_observer(ObserverType.WEATHER_DISPLAY)
        self.assertIs(retrieved_display, display)

        with self.assertRaises(ValueError):
            self.registry.create_observer("NonExistentType")

    def test_wind_speed_alert_trending(self):
        wind_alert = cast(WindSpeedAlert,
                          self.registry.create_observer(ObserverType.WIND_SPEED_ALERT))

        self.weather_station.register_observer(wind_alert)

        self.weather_station.set_measurements(28.0, 70.0, 10.0)

        self.captured_output.truncate(0)
        self.captured_output.seek(0)

        self.weather_station.set_measurements(28.0, 70.0, 15.0)

        output = self.captured_output.getvalue()

        self.assertIn("Wind speed is increasing: 10.0 km/h → 15.0 km/h", output)

        self.captured_output.truncate(0)
        self.captured_output.seek(0)

        self.weather_station.set_measurements(28.0, 70.0, 12.0)

        output = self.captured_output.getvalue()

        self.assertIn("No alert (No upward trend detected)", output)

