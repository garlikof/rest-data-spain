#!/usr/bin/env python3
import csv
import random
import uuid

AUTONOMIES = [
    {
        "name": "Andalucía",
        "lat": 37.3891,
        "lon": -5.9845,
        "provinces": [
            "Almería",
            "Cádiz",
            "Córdoba",
            "Granada",
            "Huelva",
            "Jaén",
            "Málaga",
            "Sevilla",
        ],
    },
    {
        "name": "Aragón",
        "lat": 41.6488,
        "lon": -0.8891,
        "provinces": ["Huesca", "Teruel", "Zaragoza"],
    },
    {
        "name": "Principado de Asturias",
        "lat": 43.3614,
        "lon": -5.8494,
        "provinces": ["Asturias"],
    },
    {
        "name": "Illes Balears",
        "lat": 39.6953,
        "lon": 3.0176,
        "provinces": ["Illes Balears"],
    },
    {
        "name": "País Vasco",
        "lat": 43.2627,
        "lon": -2.9253,
        "provinces": ["Álava", "Gipuzkoa", "Bizkaia"],
    },
    {
        "name": "Canarias",
        "lat": 28.2916,
        "lon": -16.6291,
        "provinces": ["Las Palmas", "Santa Cruz de Tenerife"],
    },
    {
        "name": "Cantabria",
        "lat": 43.1828,
        "lon": -3.9878,
        "provinces": ["Cantabria"],
    },
    {
        "name": "Castilla y León",
        "lat": 41.6523,
        "lon": -4.7245,
        "provinces": [
            "Ávila",
            "Burgos",
            "León",
            "Palencia",
            "Salamanca",
            "Segovia",
            "Soria",
            "Valladolid",
            "Zamora",
        ],
    },
    {
        "name": "Castilla-La Mancha",
        "lat": 39.8568,
        "lon": -4.0245,
        "provinces": ["Albacete", "Ciudad Real", "Cuenca", "Guadalajara", "Toledo"],
    },
    {
        "name": "Cataluña",
        "lat": 41.3874,
        "lon": 2.1686,
        "provinces": ["Barcelona", "Girona", "Lleida", "Tarragona"],
    },
    {
        "name": "Comunitat Valenciana",
        "lat": 39.4699,
        "lon": -0.3763,
        "provinces": ["Alicante", "Castellón", "Valencia"],
    },
    {
        "name": "Extremadura",
        "lat": 38.8794,
        "lon": -6.9707,
        "provinces": ["Badajoz", "Cáceres"],
    },
    {
        "name": "Galicia",
        "lat": 42.8805,
        "lon": -8.5457,
        "provinces": ["A Coruña", "Lugo", "Ourense", "Pontevedra"],
    },
    {
        "name": "Comunidad de Madrid",
        "lat": 40.4168,
        "lon": -3.7038,
        "provinces": ["Madrid"],
    },
    {
        "name": "Región de Murcia",
        "lat": 37.9922,
        "lon": -1.1307,
        "provinces": ["Murcia"],
    },
    {
        "name": "Comunidad Foral de Navarra",
        "lat": 42.8125,
        "lon": -1.6458,
        "provinces": ["Navarra"],
    },
    {
        "name": "La Rioja",
        "lat": 42.4627,
        "lon": -2.4449,
        "provinces": ["La Rioja"],
    },
    {
        "name": "Ceuta",
        "lat": 35.8894,
        "lon": -5.3213,
        "provinces": ["Ceuta"],
    },
    {
        "name": "Melilla",
        "lat": 35.2923,
        "lon": -2.9381,
        "provinces": ["Melilla"],
    },
]

MUNICIPALITIES_PER_PROVINCE = 5
SECTORS_PER_MUNICIPALITY = 3


def jitter(lat: float, lon: float, lat_range: float, lon_range: float) -> tuple[float, float]:
    return (
        lat + random.uniform(-lat_range, lat_range),
        lon + random.uniform(-lon_range, lon_range),
    )


def format_point(lat: float, lon: float) -> str:
    return f"({lon:.6f},{lat:.6f})"


def main() -> None:
    random.seed(42)
    words: set[str] = {"Autonomía", "Provincia", "Municipio", "Sector"}
    locations: list[dict[str, str | None]] = []

    for autonomy in AUTONOMIES:
        autonomy_id = str(uuid.uuid4())
        words.add(autonomy["name"])
        locations.append(
            {
                "id": autonomy_id,
                "name_key": autonomy["name"],
                "type": "Autonomía",
                "parent_id": None,
                "center": format_point(autonomy["lat"], autonomy["lon"]),
            }
        )

        for province_name in autonomy["provinces"]:
            province_id = str(uuid.uuid4())
            words.add(province_name)
            province_lat, province_lon = jitter(autonomy["lat"], autonomy["lon"], 1.5, 1.5)
            locations.append(
                {
                    "id": province_id,
                    "name_key": province_name,
                    "type": "Provincia",
                    "parent_id": autonomy_id,
                    "center": format_point(province_lat, province_lon),
                }
            )

            for municipality_index in range(1, MUNICIPALITIES_PER_PROVINCE + 1):
                municipality_name = f"Municipio {municipality_index} de {province_name}"
                words.add(municipality_name)
                municipality_id = str(uuid.uuid4())
                municipality_lat, municipality_lon = jitter(province_lat, province_lon, 0.4, 0.4)
                locations.append(
                    {
                        "id": municipality_id,
                        "name_key": municipality_name,
                        "type": "Municipio",
                        "parent_id": province_id,
                        "center": format_point(municipality_lat, municipality_lon),
                    }
                )

                for sector_index in range(1, SECTORS_PER_MUNICIPALITY + 1):
                    sector_name = f"Sector {sector_index} de {municipality_name}"
                    words.add(sector_name)
                    sector_id = str(uuid.uuid4())
                    sector_lat, sector_lon = jitter(municipality_lat, municipality_lon, 0.05, 0.05)
                    locations.append(
                        {
                            "id": sector_id,
                            "name_key": sector_name,
                            "type": "Sector",
                            "parent_id": municipality_id,
                            "center": format_point(sector_lat, sector_lon),
                        }
                    )

    words_path = "src/main/resources/liquibase/data/word-location.csv"
    locations_path = "src/main/resources/liquibase/data/locations.csv"

    with open(words_path, "w", encoding="utf-8", newline="") as words_file:
        writer = csv.writer(words_file)
        writer.writerow(["key"])
        for value in sorted(words):
            writer.writerow([value])

    with open(locations_path, "w", encoding="utf-8", newline="") as locations_file:
        writer = csv.DictWriter(
            locations_file,
            fieldnames=["id", "name_key", "type", "parent_id", "center"],
        )
        writer.writeheader()
        for row in locations:
            writer.writerow(row)


if __name__ == "__main__":
    main()
