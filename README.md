# Penny K8s Dashboard

Penny K8s Dashboard is a web application that provides an overview of the nodes and pods in a Kubernetes cluster. The backend retrieves Kubernetes cluster information and exposes it via REST APIs, while the frontend displays this information through an interactive Vue.js dashboard.

## Features

- Overview of Kubernetes nodes and their corresponding pods.
- Search functionality to filter pods by name.
- Ability to filter nodes by type (e.g., Master or Worker).
- Displays pod status and relevant details such as pod IP, start time, and containers.
- View logs of individual pods directly in the UI.

## Tech Stack

- **Frontend**:
  - Vue 3
  - Tailwind CSS
  - Axios (for API requests)

- **Backend**:
  - Java (Spring Boot)
  - Kubernetes Client for Java

- **Containerization**:
  - Docker
  - Helm for Kubernetes deployment

## Getting Started



Before running the project, ensure you have the following installed:

- **Docker**: For containerizing the application.
- **Helm**: For deploying to Kubernetes.
- **Node.js & npm**: For running the frontend application.
- **Java & Maven**: For building the backend application.
- **Kubernetes Cluster**: A running Kubernetes cluster to fetch the data from.

### How it Works
## Backend:

The backend is a Spring Boot application that communicates with the Kubernetes API to retrieve data about nodes and pods.

It provides an endpoint that returns the list of nodes and their associated pods.

## Frontend:

The frontend is a Vue.js application that queries the backend API for node and pod information.

Users can filter nodes by type (Master or Worker) and search pods by name.

The UI is built using Tailwind CSS for a responsive and clean design.

Pod logs can be viewed directly from the dashboard.

## Interaction:

The frontend fetches data from the backend on page load using Axios.

It displays the nodes and their pods in a collapsible, expandable format.

The searchTerm input allows users to filter pods by name, and the tabs filter by node type.
