import { createContext, useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import toast from "react-hot-toast"; 
import { WS_URL } from "../util/apiEndpoints";

export const AppContext = createContext();

export const AppContextProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [transactionTrigger, setTransactionTrigger] = useState(0);
    
    // --- NEW CHAT STATE ---
    const [chatMessages, setChatMessages] = useState([]);
    const stompClientRef = useRef(null); // Keep reference to client to send messages

    const clearUser = () => {
        setUser(null);
        setChatMessages([]);
    };
useEffect(() => {
        if (!user) return;

        const basePath = user.groupId 
            ? `/topic/groups/${user.groupId}` 
            : `/topic/user/${user.id}`;

        const client = new Client({
            webSocketFactory: () => new SockJS(WS_URL),
            onConnect: () => {
                console.log("Connected to WebSocket");

                // 1. Expenses & Settlements (UPDATED LOGIC)
                client.subscribe(`${basePath}/expenses`, (message) => {
                    // Check the actual message content sent from Backend
                    if (message.body === "Expense Added") {
                        toast.success("New Expense in Group!");
                    } 
                    // If it is "Debt Settled", we DO NOT show a toast, 
                    // but we still refresh the data.
                    
                    setTransactionTrigger(prev => prev + 1);
                });
                
                // 2. Incomes (Existing)
                client.subscribe(`${basePath}/incomes`, () => {
                    toast.success("New Income in Group!"); 
                    setTransactionTrigger(prev => prev + 1);
                });

                // 3. Chat (Existing)
                if (user.groupId) {
                    client.subscribe(`/topic/groups/${user.groupId}/chat`, (message) => {
                        const receivedMsg = JSON.parse(message.body);
                        setChatMessages(prev => [...prev, receivedMsg]);
                    });
                }
            },
            onStompError: (frame) => {
                console.error("Broker reported error: " + frame.headers["message"]);
            },
        });

        client.activate();
        stompClientRef.current = client;

        return () => {
            client.deactivate();
        };
    }, [user]); 

    // --- HELPER TO SEND MESSAGE ---
    const sendChatMessage = (content) => {
        if (stompClientRef.current && user?.groupId) {
            const chatMessage = {
                content: content,
                senderName: user.fullName,
                senderEmail: user.email,
                groupId: user.groupId
            };
            
            stompClientRef.current.publish({
                destination: `/app/chat/${user.groupId}`,
                body: JSON.stringify(chatMessage)
            });
        }
    };

    const contextValue = {
        user,
        setUser,
        clearUser,
        transactionTrigger,
        setTransactionTrigger, // <--- ADDED THIS LINE (The Fix)
        chatMessages,       
        setChatMessages,    
        sendChatMessage     
    };

    return (
        <AppContext.Provider value={contextValue}>
            {children}
        </AppContext.Provider>
    );
};