from typing import List, Tuple

from app.alerts.base_alert import BaseAlert
from app.weather_data import AlertType, WeatherData


class WindSpeedAlert(BaseAlert):
    def __init__(self) -> None:
        super().__init__()
        self.alert_type = AlertType.WIND_SPEED
        self._wind_speed_history: List[float] = []

    def _check_alert_conditions(self, weather_data: WeatherData) -> Tuple[bool, str]:
        current_wind_speed = weather_data.wind_speed

        self._wind_speed_history.append(current_wind_speed)
        if len(self._wind_speed_history) > 3:
            self._wind_speed_history.pop(0)

        if len(self._wind_speed_history) >= 2:
            previous_wind_speed = self._wind_speed_history[-2]

            if current_wind_speed > previous_wind_speed:
                return True, (f"Wind speed is increasing: {previous_wind_speed} km/h → "
                              f"{current_wind_speed} km/h")

        return False, ""

    def _no_alert_needed(self, weather_data: WeatherData) -> None:
        if len(self._wind_speed_history) < 2:
            print(f"{self.__class__.__name__}: No alert (Insufficient data)")
        else:
            print(f"{self.__class__.__name__}: No alert (No upward trend detected)")
