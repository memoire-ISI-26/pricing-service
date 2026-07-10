# pricing-service

Ce microservice gère le **catalogue d'offres** (Pass Internet, Illimix, Illiflex) ainsi que la gestion et recharge des cartes de péage/transport **Rapido**.

## ⚙️ Rôle et Fonctionnalités

- **Catalogue de Pass** : Gestion des différents types de pass (Internet, Illimix et pass personnalisables Illiflex).
- **Cartes Rapido** : Enregistrement et consultation du solde des cartes Rapido (identifiées par un numéro à 10 chiffres).
- **Traitement des Achats** (`PurchaseService`) :
  - Validation de l'existence de l'acheteur et du destinataire en interrogeant `user-service` via Feign.
  - Débit du solde du portefeuille en appelant `wallet-service` via Feign.
  - Mise à jour du solde de la carte Rapido (en cas de recharge Rapido).
  - Envoi d'événements de tracking (`ACHAT_PASS_INTERNET`, `ACHAT_PASS_ILLIMIX`, `ACHAT_CREDIT`, `ACHAT_RAPIDO`) au `tracking-service`.

---

## 🔌 Configuration et Endpoints

- **Port par défaut** : `8201`
- **Base de données** : MySQL (`pricing_service_db`), configurée via JPA/Hibernate.
- **Technologie** : Spring Boot, JPA, Netflix Eureka Client, Feign Client (User, Wallet, Tracking)

### Endpoints principaux :

#### 1. Consultation des offres
* `GET /pricing/pass-internet` : Liste tous les pass Internet disponibles.
* `GET /pricing/pass-illimix` : Liste tous les pass Illimix disponibles.
* `GET /pricing/pass-illiflex` : Liste tous les pass personnalisables Illiflex.

#### 2. Processus d'achat
* **Achat Pass Internet** : `POST /pricing/purchase/pass-internet`
* **Achat Pass Illimix** : `POST /pricing/purchase/pass-illimix`
* **Achat Crédit Téléphonique** : `POST /pricing/purchase/credit` (Débite le solde principal pour créditer le crédit d'appel).
* **Corps de la requête (JSON)** :
  ```json
  {
    "receiverNumber": "771234567",
    "passId": 1,
    "amount": 1000.0,
    "paymentMethod": "WALLET"
  }
  ```

#### 3. Gestion Rapido
* `GET /pricing/rapido/card/{cardNumber}` : Vérifie l'existence et renvoie le solde d'une carte Rapido.
* `POST /pricing/rapido/register` : Crée une nouvelle carte Rapido avec un solde initial.
* `POST /pricing/purchase/rapido` : Recharge une carte Rapido en débitant le compte principal (Wallet) du client.

---

## 🔗 Liens Feign Clients

Ce service interagit avec :
- **`UserProxy`** (`user-service`) : Pour s'assurer que les numéros d'expéditeur et de destinataire correspondent à des comptes clients réels.
- **`WalletProxy`** (`wallet-service`) : Pour effectuer le prélèvement financier (débit) sur le portefeuille virtuel du client.
- **`TrackingProxy`** (`tracking-service`) : Pour enregistrer l'achat dans l'historique d'audit global.
