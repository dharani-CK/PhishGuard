// <!-- ========================= script.js ========================= -->
let currentUser = null;
let credentials = [];

const API_BASE = "http://localhost:8080";

// ================= AUTH =================
async function login() {
  const username = document.getElementById('loginUser').value;
  const password = document.getElementById('loginPass').value;

  if (!username || !password) {
    showAlert('Enter username and password', 'warning');
    return;
  }

  const res = await fetch(`${API_BASE}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });

  const data = await res.json();

  if (!data || !data.id) {
    showAlert('Invalid login', 'danger');
    return;
  }

  currentUser = data;
  showAlert('Login successful', 'safe');

  document.getElementById('loginPage').classList.add('hidden');
  document.getElementById('dashboard').classList.remove('hidden');

  loadCredentials();
}

function showRegister() {
  document.getElementById('loginPage').classList.add('hidden');
  document.getElementById('registerPage').classList.remove('hidden');
}

function showLogin() {
  document.getElementById('registerPage').classList.add('hidden');
  document.getElementById('loginPage').classList.remove('hidden');
}

async function register() {
  const username = document.getElementById('regUser').value;
  const password = document.getElementById('regPass').value;

  if (!username || !password) {
    showAlert('Fill all fields', 'warning');
    return;
  }

  await fetch(`${API_BASE}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password })
  });

  showAlert('Account created', 'safe');
  showLogin();
}

function logout() {
  currentUser = null;
  credentials = [];
  document.getElementById('dashboard').classList.add('hidden');
  document.getElementById('loginPage').classList.remove('hidden');
}

// ================= CREDENTIALS =================
async function addCredential() {
  const url = document.getElementById('url').value;
  const username = document.getElementById('siteUser').value;
  const password = document.getElementById('sitePass').value;

  if (!url || !username || !password) {
    showAlert('Fill all fields', 'warning');
    return;
  }

  const status = checkPhishing(url);

  await fetch(`${API_BASE}/credentials`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      url,
      username,
      password,
      user: { id: currentUser.id }
    })
  });

  showAlert('Credential saved', 'safe');
  loadCredentials();
}

async function loadCredentials() {
  const res = await fetch(`${API_BASE}/credentials/${currentUser.id}`);
  credentials = await res.json();
  renderCredentials();
}

function renderCredentials() {
  const list = document.getElementById('credList');
  const search = document.getElementById('search').value.toLowerCase();

  list.innerHTML = '';

  credentials
    .filter(c => c.url.toLowerCase().includes(search))
    .forEach(c => {
      const status = checkPhishing(c.url);

      const div = document.createElement('div');
      div.className = 'credential';

      div.innerHTML = `
        <div>
          <strong>${c.url}</strong><br>
          <small>${c.username}</small>
        </div>
        <div>
          <span class="badge ${status}">${status}</span>
          <button onclick="deleteCredential(${c.id})">Delete</button>
        </div>
      `;

      list.appendChild(div);
    });
}

async function deleteCredential(id) {
  await fetch(`${API_BASE}/credentials/${id}`, {
    method: "DELETE"
  });

  showAlert('Deleted', 'danger');
  loadCredentials();
}

// ================= PHISHING =================
async function reportPhishing() {
  const url = document.getElementById('phishUrl').value;

  if (!url) {
    showAlert('Enter URL', 'warning');
    return;
  }

  await fetch(`${API_BASE}/phishing`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ url })
  });

  showAlert('Phishing reported', 'danger');
}

function checkPhishing(url) {
  if (!url.startsWith('https')) return 'danger';
  if (url.includes('login') || url.includes('verify')) return 'warning';
  return 'safe';
}

// ================= ALERT =================
function showAlert(msg, type) {
  const box = document.getElementById('alertBox');
  box.innerHTML = `<div class="alert ${type}">${msg}</div>`;
  setTimeout(() => box.innerHTML = '', 3000);
}