# 📦 Inventory Management API

Une application RESTful développée en Java avec Spring Boot permettant de gérer un inventaire de produits. Elle fournit des endpoints pour créer, lire, mettre à jour et supprimer des produits, ainsi que pour suivre les stocks faibles.

---

## 🚀 Fonctionnalités

- 🔍 Consulter tous les produits
- ➕ Créer un nouveau produit
- ✏️ Mettre à jour un produit existant
- ❌ Supprimer un produit
- ⚠️ Lister les produits en faible stock (stock < 5)

---

## 🛠️ Technologies utilisées

- Java 17+
- Spring Boot
- Spring Web
- Spring Validation
- Swagger (OpenAPI 3) pour la documentation
- Maven ou Gradle
- IDE recommandé : IntelliJ IDEA ou Eclipse

---

## 📂 Structure des classes principales

| Classe              | Description                                  |
|---------------------|----------------------------------------------|
| `ProductDTO`        | Objet de transfert pour les produits         |
| `ProductController` | Expose les endpoints REST de l'API          |
| `ProductService`    | Contient la logique métier liée aux produits|
| `ProductResponse`   | Réponse paginée contenant les produits et alertes |

---

## 🧪 Comment lancer l’application

```bash
# Cloner le dépôt
git clone  git@github-email1:belviPouadjeu/Product-API.git

# Accéder au dossier
cd Product-API.git

# Construire le projet
./mvnw clean install

# Lancer le projet
./mvnw spring-boot:run

## 📘 Documentation Swagger

### Accéder à la documentation interactive :

```bash
http://localhost:8082/swagger-ui/index.html
```
# 📬 Endpoints principaux

## 🔹 GET /products
Retourne la liste de tous les produits :

```json
{
  "content": [
    {
      "id": 1,
      "name": "Smartphone",
      "price": 499.99,
      "stockQuantity": 4
    }
  ],
  "alerts": [
    "⚠️ Stock is low for product: Smartphone"
  ]
}
```
### 🔹 PUT /products/{id}

**Mettre à jour un produit existant :**

```json
{
  "name": "Tablet Pro",
  "price": 399.99,
  "stockQuantity": 7
}
```
### Réponse : 200 OK

🔹 **DELETE /products/{id}**  
Supprime le produit correspondant à l’ID donné.

**Réponse :**  
- 200 OK  
- ou 404 Not Found si non trouvé.

---
🔹 **GET /products/low-stock**  
Retourne les produits dont la quantité en stock est inférieure à 5 :

```json
[
  {
    "name": "Laptop",
    "price": 899.99,
    "stockQuantity": 2
  }
]
```

### ✅ Validation

- Le champ `name` est obligatoire et doit être unique.
- Le champ `price` doit être positif.
- Le champ `stockQuantity` doit être ≥ 0.

📌 **Auteur**  
Développé par Belvinard Dev  
📧 Contact : belvinard97mail@gmail.com

📄 **Licence**  
Ce projet est open-source et libre d’utilisation. Licence MIT.
