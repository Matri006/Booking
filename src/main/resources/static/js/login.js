async function login() {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email,
            password
        })
    });

    const data = await response.json();

    if (response.ok) {

        localStorage.setItem("token", data.token);

        window.location.href = "/dashboard";

    } else {

        document.getElementById("error").innerText =
            data.message || "Login failed";
    }
}