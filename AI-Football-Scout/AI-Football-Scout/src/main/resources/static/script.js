async function askAI() {
    const inputField = document.getElementById('user-input');
    const chatBox = document.getElementById('chat-box');
    const message = inputField.value.trim();

    if (!message) return;

    chatBox.innerHTML += `<p><b>Bạn:</b> ${message}</p>`;
    inputField.value = '';

    const loadingId = "loading-" + Date.now();
    chatBox.innerHTML += `<p id="${loadingId}"><i>AI Scout đang phân tích...</i></p>`;
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
        const positions = ['LW', 'RW', 'ST', 'CF', 'CAM', 'AM', 'CM', 'CDM', 'DM', 'CB', 'LB', 'RB', 'LWB', 'RWB', 'GK', 'LM', 'RM'];
        const inputUpper = message.toUpperCase(); 
        
        let apiUrl = "";
        let isDiscoverMode = false;
        let detectedPosition = "";
        for (let pos of positions) {
            let regex = new RegExp(`\\b${pos}\\b`);
            if (regex.test(inputUpper)) {
                isDiscoverMode = true;
                detectedPosition = pos; 
                break; 
            }
        }
        if (isDiscoverMode) {
            apiUrl = `/api/scout/discover?position=${detectedPosition}`;
        } else {
            apiUrl = `/api/scout/analyze?playerName=${encodeURIComponent(message)}`;
        }
        const response = await fetch(apiUrl);
        document.getElementById(loadingId).remove();

        if (isDiscoverMode) {
            const data = await response.json(); 
            let htmlList = `<b>Đã tìm thấy 5 siêu sao ở vị trí ${detectedPosition}:</b><br><ul style="list-style-type: none; padding-left: 0;">`;
            
            data.forEach(player => {
                htmlList += `
                <li style="margin-bottom: 8px;">
                    <b>${player.name}</b> <i>(${player.club})</i> 
                    <button onclick="analyzeDirectly('${player.name}')" 
                            style="margin-left: 10px; padding: 2px 8px; cursor: pointer; background: #007bff; color: white; border: none; border-radius: 4px;">
                        🔍 Phân tích
                    </button>
                </li>`;
            });
            
            htmlList += `</ul>`;
            chatBox.innerHTML += `<p><b>AI Scout:</b> ${htmlList}</p>`;
            
        } else {
            const data = await response.text();
            const formattedText = data.replace(/\n/g, "<br>");
            chatBox.innerHTML += `<p><b>AI Scout:</b> ${formattedText}</p>`;
        }
        
        chatBox.scrollTop = chatBox.scrollHeight;

    } catch (error) {
        document.getElementById(loadingId).innerText = "Lỗi: Không kết nối được Backend!";
        console.error("Toang rồi:", error);
    }
}

document.querySelector('button').onclick = askAI;
function analyzeDirectly(realName) {
    document.getElementById('user-input').value = realName;
    askAI(); 
}