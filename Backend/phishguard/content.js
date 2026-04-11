console.log(" PhishGuard Running");

let attempts = 0;

const interval = setInterval(async () => {
    attempts++;
    console.log(" Attempt:", attempts);

    // 🔥 Better field detection (Facebook safe)
    const usernameField =
        document.querySelector('input[name="email"]') ||
        document.querySelector('input[id="email"]') ||
        document.querySelector('input[type="text"]');

    const passwordField =
        document.querySelector('input[name="pass"]') ||
        document.querySelector('input[id="pass"]') ||
        document.querySelector('input[type="password"]');

    if (usernameField && passwordField) {
        console.log(" Login fields found");

        clearInterval(interval);

        try {
            //  Fetch credentials
            const res = await fetch("http://localhost:8088/phishguard/getCredentials?userId=43",{
				credentials: "include"
			});
            const data = await res.json();

            console.log("Data:", data);

            if (!data.length) {
                console.log(" No credentials found");
                return;
            }

            // 🔥 Strong URL matching
            const currentHost = window.location.hostname;

            const match = data.find(item => {
                try {
                    const url = new URL(item.website);
                    return currentHost.includes(url.hostname);
                } catch {
                    return currentHost.includes(item.website);
                }
            });

            console.log("Matched:", match);

            if (!match) {
                console.log(" No matching site");
                return;
            }

            // 🔥 React-safe setter (IMPORTANT FIX)
            function setNativeValue(element, value) {
                const prototype = Object.getPrototypeOf(element);
                const descriptor = Object.getOwnPropertyDescriptor(prototype, "value");

                if (descriptor && descriptor.set) {
                    descriptor.set.call(element, value);
                } else {
                    element.value = value;
                }

                element.dispatchEvent(new Event("input", { bubbles: true }));
                element.dispatchEvent(new Event("change", { bubbles: true }));
                element.dispatchEvent(new Event("blur", { bubbles: true }));
            }

            //  Delay (VERY IMPORTANT for Facebook)
            setTimeout(() => {
                setNativeValue(usernameField, match.username);
                setNativeValue(passwordField, match.password);

                console.log("🎉 Autofill success");
            }, 500);

        } catch (err) {
            console.error(" Error:", err);
        }
    }

    // 🔥 More retries (Facebook loads slowly)
    if (attempts > 20) {
        clearInterval(interval);
        console.log(" Fields not found");
    }

}, 1000);
