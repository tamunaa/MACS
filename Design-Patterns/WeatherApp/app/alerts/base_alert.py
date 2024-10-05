import abc
import time
from typing import Optional, Tuple

from app.weather_data import AlertType, WeatherData


class BaseAlert(abc.ABC):

    def __init__(self) -> None:
        self.alert_type = AlertType.GENERAL
        self._last_alert_time: Optional[float] = None

    def update(self, weather_data: WeatherData) -> None:
        should_alert, message = self._check_alert_conditions(weather_data)

        if should_alert:
            self._last_alert_time = time.time()
            self._trigger_alert(message)
        else:
            self._no_alert_needed(weather_data)

    @abc.abstractmethod
    def _check_alert_conditions(self, weather_data: WeatherData) -> Tuple[bool, str]:
        pass

    def _trigger_alert(self, message: str) -> None:
        print(f"{self.__class__.__name__}: **Alert! {message}**")

    def _no_alert_needed(self, weather_data: WeatherData) -> None:
        print(f"{self.__class__.__name__}: No alert (conditions normal)")
