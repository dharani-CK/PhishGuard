// ===============================
// GLOBAL FUNCTIONS
// ===============================
function openModal() {
document.getElementById("modal").style.display = "flex";
}

function closeModal() {
document.getElementById("modal").style.display = "none";
}

function closeWarning() {
document.getElementById("warningModal").style.display = "none";
}


// ===============================
// MAIN LOGIC
// ===============================
document.addEventListener("DOMContentLoaded", function () {

console.log("✅ JS Loaded");

const form = document.getElementById("credentialForm");

if (!form) {
console.error("❌ Form NOT found!");
return;
}

console.log("✅ Form found");

form.addEventListener("submit", function (e) {
e.preventDefault();

console.log("🚀 FORM SUBMITTED");

// ===============================
// SAFE INPUT FETCH
// ===============================
const websiteInput = document.getElementById("website");
const usernameInput = document.getElementById("site_username");
const passwordInput = document.getElementById("password");

if (!websiteInput || !usernameInput || !passwordInput) {
console.error("❌ One or more inputs NOT found");
alert("Error: Inputs not found!");
return;
}

const website = websiteInput.value.trim();
const username = usernameInput.value.trim();
const password = passwordInput.value.trim();

console.log("📦 Data:", website, username, password);

// ===============================
// VALIDATION
// ===============================
if (!website || !username || !password) {
alert("❌ All fields are required!");
return;
}

// ===============================
// PHISHING CHECK
// ===============================
if (!website.startsWith("https://")) {
console.warn("⚠ Non-HTTPS detected");
document.getElementById("warningModal").style.display = "flex";
return;
}

// ===============================
// PREPARE DATA
// ===============================
const formData = new FormData();
formData.append("website", website);
formData.append("site_username", username);
formData.append("password", password);

console.log("📡 Sending request...");

// ===============================
// SEND REQUEST
// ===============================
fetch("addCredential", {
method: "POST",
body: formData,
credentials: "include"
})
.then(response => {
console.log("📡 Response Status:", response.status);

if (response.ok) {
alert("✅ Saved successfully!");
closeModal();
location.reload();
} else {
alert("❌ Error saving data");
}
})
.catch(error => {
console.error("🔥 Fetch Error:", error);
alert("❌ Server error");
});

});

});

// LOAD SAVED DATA
function loadCredentials() {
fetch("/phishguard/getCredentials", {
method: "GET",
credentials: "include"
})
.then(res => res.json())
.then(data => {
const container = document.getElementById("credentialsContainer");
container.innerHTML = "";

data.forEach(item => {
const card = document.createElement("div");
card.className = "card";

card.innerHTML = `
    <div>
        <strong>${item.website}</strong><br>
        Username: ${item.username}<br>
        <span id="password-${item.id}" style="display:none;">
            Password: ********
        </span>
    </div>
    <div>
        <button onclick="viewPassword(${item.id})">View</button>
        <button onclick="deleteCredential(${item.id})" style="background:red;">Delete</button>
    </div>
`;

container.appendChild(card);
});
})
.catch(err => console.error(err));
}

// CALL ON PAGE LOAD
document.addEventListener("DOMContentLoaded", function () {
loadCredentials();
});
function viewPassword(id) {

    const passElement = document.getElementById("password-" + id);

    // 🔁 Toggle hide/show
    if (passElement.style.display === "block") {
        passElement.style.display = "none";
        return;
    }

    fetch("getPassword?id=" + id, {
        method: "GET",
        credentials: "include"
    })
    .then(res => res.text())
    .then(data => {
        passElement.style.display = "block";
        passElement.innerHTML = "Password: " + data;
    })
    .catch(err => {
        console.error(err);
    });
}
function deleteCredential(id) {

    if (!confirm("Are you sure you want to delete this credential?")) {
        return;
    }

    fetch("deleteCredential?id=" + id, {
        method: "DELETE",
        credentials: "include"
    })
    .then(res => {
        if (res.ok) {
            alert("✅ Deleted successfully");
            location.reload(); // refresh list
        } else {
            alert("❌ Failed to delete");
        }
    })
    .catch(err => {
        console.error(err);
        alert("❌ Server error");
    });
}
