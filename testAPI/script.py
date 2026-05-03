import requests
import json

# Configurazione
BASE_URL = "https://gare-oct.run.place/votazione-fotografica"
API_URL = f"{BASE_URL}/admin/getngiudici"

# Codice API (deve coincidere con quello in VotoSectionController.java)
API_CODE = "YOUR_API_KEY_HERE"

def test_get_classifica():
    print(f"Richiesta classifica a: {API_URL}...")

    # Effettua la chiamata all'endpoint della classifica (ora permitAll)
    params = {"apicode": API_CODE}

    try:
        response = requests.get(API_URL, params=params)

        if response.status_code == 200:
            print("Classifica ricevuta correttamente:")
            try:
                classifica = response.json()
                print(json.dumps(classifica, indent=4))
            except json.decoder.JSONDecodeError as e:
                print(f"Errore nella decodifica JSON: {e}")
                print("Contenuto della risposta (probabilmente non è JSON):")
                print(response.text[:1000])
        elif response.status_code == 400:
            print("Errore: API Code non valido (400 Bad Request)")
        elif response.status_code == 403:
            print("Errore: Accesso negato (403 Forbidden). Verifica le configurazioni di sicurezza.")
        else:
            print(f"Errore nella chiamata API: {response.status_code}")
            print(response.text[:500])

    except requests.exceptions.ConnectionError:
        print("Errore: Impossibile connettersi al server. Assicurati che sia attivo su localhost.")

def test_get_votazioni():
    params = {"apicode": API_CODE}
    response = requests.get(API_URL, params=params)
    print(response.text)

if __name__ == "__main__":
    test_get_votazioni()
