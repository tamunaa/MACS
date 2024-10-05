from app.simulate import WeatherSimulation


def main() -> None:
    simulation = WeatherSimulation(weeks=20)
    simulation.run()


if __name__ == "__main__":
    main()
