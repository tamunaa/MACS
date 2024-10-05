import unittest
from unittest.mock import MagicMock, patch

from app.alerts.temperature_alert import TemperatureAlert
from app.alerts.wind_speed_alert import WindSpeedAlert
from app.weather_data import Observer, WeatherData
from app.weather_station import WeatherStation


class TestWeatherData(unittest.TestCase):
    def test_initialization(self) -> None:
        data = WeatherData(temperature=28.0, humidity=70.0, wind_speed=12.0)
        self.assertEqual(data.temperature, 28.0)
        self.assertEqual(data.humidity, 70.0)
        self.assertEqual(data.wind_speed, 12.0)
        self.assertIsNotNone(data.timestamp)

    def test_to_dict(self) -> None:
        data = WeatherData(temperature=28.0, humidity=70.0, wind_speed=12.0)
        data_dict = data.to_dict()
        self.assertEqual(data_dict["temperature"], 28.0)
        self.assertEqual(data_dict["humidity"], 70.0)
        self.assertEqual(data_dict["wind_speed"], 12.0)
        self.assertIn("timestamp", data_dict)


class TestWeatherStation(unittest.TestCase):
    def setUp(self) -> None:
        self.weather_station = WeatherStation()
        self.mock_observer = MagicMock(spec=Observer)

    def test_initial_values(self) -> None:
        """Test initial weather values."""
        self.assertEqual(self.weather_station.temperature, 28.0)
        self.assertEqual(self.weather_station.humidity, 70.0)
        self.assertEqual(self.weather_station.wind_speed, 12.0)

    def test_current_data_property(self) -> None:
        data = self.weather_station.current_data
        self.assertIsInstance(data, WeatherData)
        self.assertEqual(data.temperature, 28.0)
        self.assertEqual(data.humidity, 70.0)
        self.assertEqual(data.wind_speed, 12.0)

    def test_set_measurements(self) -> None:
        self.weather_station.set_measurements(30.0, 75.0, 15.0)
        self.assertEqual(self.weather_station.temperature, 30.0)
        self.assertEqual(self.weather_station.humidity, 75.0)
        self.assertEqual(self.weather_station.wind_speed, 15.0)

    def test_register_observer(self) -> None:
        self.weather_station.register_observer(self.mock_observer)

        self.mock_observer.update.assert_called_once()

        self.mock_observer.reset_mock()
        self.weather_station.set_measurements(30.0, 75.0, 15.0)

        self.mock_observer.update.assert_called_once()
        call_args = self.mock_observer.update.call_args[0][0]
        self.assertEqual(call_args.temperature, 30.0)
        self.assertEqual(call_args.humidity, 75.0)
        self.assertEqual(call_args.wind_speed, 15.0)

    def test_remove_observer(self) -> None:
        self.weather_station.register_observer(self.mock_observer)

        self.mock_observer.reset_mock()
        self.weather_station.remove_observer(self.mock_observer)

        self.weather_station.set_measurements(32.0, 80.0, 18.0)

        self.mock_observer.update.assert_not_called()

    def test_data_history(self) -> None:
        self.assertEqual(len(self.weather_station.data_history), 1)

        self.weather_station.set_measurements(30.0, 75.0, 15.0)
        self.weather_station.set_measurements(32.0, 80.0, 18.0)

        history = self.weather_station.data_history
        self.assertEqual(len(history), 3)

        self.assertEqual(history[0].temperature, 28.0)
        self.assertEqual(history[1].temperature, 30.0)
        self.assertEqual(history[2].temperature, 32.0)

        history.pop()
        self.assertEqual(len(self.weather_station.data_history), 3)


class TestTemperatureAlert(unittest.TestCase):

    def setUp(self) -> None:
        self.alert = TemperatureAlert()
        self.alert._threshold = 32.0

    def test_alert_triggered(self) -> None:
        with patch('builtins.print') as mock_print:
            data = WeatherData(temperature=33.0, humidity=75.0, wind_speed=15.0)
            self.alert.update(data)

            mock_print.assert_called_once()
            call_args = mock_print.call_args[0][0]
            self.assertIn("Alert", call_args)
            self.assertIn("32.0°C", call_args)
            self.assertIn("33.0°C", call_args)

    def test_alert_not_triggered(self) -> None:
        with patch('builtins.print') as mock_print:
            data = WeatherData(temperature=30.0, humidity=75.0, wind_speed=15.0)
            self.alert.update(data)

            mock_print.assert_called_once()
            call_args = mock_print.call_args[0][0]
            self.assertIn("No alert", call_args)


class TestWindSpeedAlert(unittest.TestCase):

    def setUp(self) -> None:
        self.alert = WindSpeedAlert()

    def test_insufficient_data(self) -> None:
        with patch('builtins.print') as mock_print:
            data = WeatherData(temperature=30.0, humidity=75.0, wind_speed=15.0)
            self.alert.update(data)

            mock_print.assert_called_once()
            call_args = mock_print.call_args[0][0]
            self.assertIn("Insufficient data", call_args)

        self.assertEqual(len(self.alert._wind_speed_history), 1)

    def test_alert_triggered(self) -> None:
        data1 = WeatherData(temperature=30.0, humidity=75.0, wind_speed=15.0)
        self.alert.update(data1)

        with patch('builtins.print') as mock_print:
            data2 = WeatherData(temperature=31.0, humidity=76.0, wind_speed=18.0)
            self.alert.update(data2)

            mock_print.assert_called_once()
            call_args = mock_print.call_args[0][0]
            self.assertIn("Alert", call_args)
            self.assertIn("15.0 km/h → 18.0 km/h", call_args)

    def test_alert_not_triggered(self) -> None:
        data1 = WeatherData(temperature=30.0, humidity=75.0, wind_speed=15.0)
        self.alert.update(data1)

        with patch('builtins.print') as mock_print:
            data2 = WeatherData(temperature=31.0, humidity=76.0, wind_speed=12.0)
            self.alert.update(data2)

            mock_print.assert_called_once()
            call_args = mock_print.call_args[0][0]
            self.assertIn("No alert", call_args)
            self.assertIn("No upward trend detected", call_args)


if __name__ == '__main__':
    unittest.main()
