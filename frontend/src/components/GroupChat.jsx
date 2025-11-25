import { useState, useEffect, useContext, useRef } from "react";
import { Send, MessageCircle } from "lucide-react";
import { AppContext } from "../context/AppContext.jsx";
import axiosConfig from "../util/axiosConfig.jsx";
import moment from "moment";

const GroupChat = () => {
    const { user, chatMessages, setChatMessages, sendChatMessage } = useContext(AppContext);
    const [input, setInput] = useState("");
    const scrollRef = useRef(null);

    // 1. Load History on Mount
    useEffect(() => {
        const fetchHistory = async () => {
            if (user?.groupId) {
                try {
                    // We only fetch if list is empty to avoid overwriting live data
                    if (chatMessages.length === 0) {
                        const res = await axiosConfig.get(`/chat/history/${user.groupId}`);
                        setChatMessages(res.data);
                    }
                } catch (error) {
                    console.error("Failed to load chat history");
                }
            }
        };
        fetchHistory();
    }, [user]);

    // 2. Auto-scroll to bottom when new message arrives
    useEffect(() => {
        scrollRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [chatMessages]);

    const handleSend = (e) => {
        e.preventDefault();
        if (!input.trim()) return;
        sendChatMessage(input);
        setInput("");
    };

    return (
        <div className="flex flex-col h-[350px]"> {/* Fixed height for the chat area */}
            
            {/* Messages Area */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4 custom-scrollbar bg-gray-50/50 rounded-t-xl">
                {chatMessages.length === 0 ? (
                    <div className="text-center text-gray-400 mt-10">
                        <MessageCircle size={40} className="mx-auto mb-2 opacity-20" />
                        <p className="text-sm">No messages yet. Start the conversation!</p>
                    </div>
                ) : (
                    chatMessages.map((msg, idx) => {
                        const isMe = msg.senderEmail === user.email;
                        return (
                            <div key={idx} className={`flex ${isMe ? 'justify-end' : 'justify-start'}`}>
                                <div className={`max-w-[80%] ${isMe ? 'items-end' : 'items-start'} flex flex-col`}>
                                    <div 
                                        className={`px-4 py-2 rounded-2xl text-sm shadow-sm ${
                                            isMe 
                                                ? 'bg-purple-600 text-white rounded-br-none' 
                                                : 'bg-white border border-gray-200 text-gray-800 rounded-bl-none'
                                        }`}
                                    >
                                        {msg.content}
                                    </div>
                                    <span className="text-[10px] text-gray-400 mt-1 px-1">
                                        {!isMe && <span className="font-semibold mr-1">{msg.senderName}</span>}
                                        {moment(msg.timestamp).format("h:mm A")}
                                    </span>
                                </div>
                            </div>
                        );
                    })
                )}
                <div ref={scrollRef} />
            </div>

            {/* Input Area */}
            <form onSubmit={handleSend} className="p-3 bg-white border-t border-gray-100 flex gap-2">
                <input
                    type="text"
                    placeholder="Type a message..."
                    className="flex-1 bg-gray-100 border-none rounded-full px-4 py-2 text-sm focus:ring-2 focus:ring-purple-500 outline-none"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                />
                <button 
                    type="submit"
                    disabled={!input.trim()}
                    className="w-10 h-10 bg-purple-600 text-white rounded-full flex items-center justify-center hover:bg-purple-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
                >
                    <Send size={18} />
                </button>
            </form>
        </div>
    );
};

export default GroupChat;