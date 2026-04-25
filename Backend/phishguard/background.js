chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {

    if (request.type === "GET_CREDENTIALS") {

        fetch("http://localhost:8088/phishguard/getCredentials?userId=43")
            .then(res => res.json())
            .then(data => {
                console.log("✅ Background received:", data);
                sendResponse(data);
            })
            .catch(err => {
                console.error("❌ Background error:", err);
                sendResponse([]);
            });

        return true; // required
    }
});
