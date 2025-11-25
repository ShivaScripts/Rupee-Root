import { useState, useRef, useEffect, useContext } from "react";
import { User, LogOut, Users, X, Menu, PiggyBank, Wallet } from "lucide-react"; // Added Wallet
import { useNavigate } from "react-router-dom";
import { assets } from "../assets/assets.js";
import { AppContext } from "../context/AppContext.jsx";
import Sidebar from "./Sidebar.jsx";
import axiosConfig from "../util/axiosConfig.jsx";
import { API_ENDPOINTS } from "../util/apiEndpoints.js";
import toast from "react-hot-toast";

const Menubar = ({ activeMenu }) => {
    const [openSideMenu, setOpenSideMenu] = useState(false);
    const [showDropdown, setShowDropdown] = useState(false);
    
    // Modal States
    const [showGroupModal, setShowGroupModal] = useState(false);
    const [showBudgetModal, setShowBudgetModal] = useState(false); // New State
    
    // Inputs
    const [joinCode, setJoinCode] = useState("");
    const [inviteEmail, setInviteEmail] = useState("");
    const [budgetAmount, setBudgetAmount] = useState(""); // New Input State

    const dropdownRef = useRef(null);
    const { clearUser, user, setUser } = useContext(AppContext);
    const navigate = useNavigate();

    // Initialize budget input when user data loads
    useEffect(() => {
        if (user?.budgetLimit) {
            setBudgetAmount(user.budgetLimit);
        }
    }, [user]);

    // Handle click outside
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowDropdown(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("token");
        clearUser();
        navigate("/login");
    };

    const handleCreateGroup = async () => {
        try {
            const response = await axiosConfig.post(API_ENDPOINTS.CREATE_GROUP);
            if (response.status === 200) {
                toast.success("Group created successfully!");
                setUser(prev => ({...prev, groupId: response.data.groupId}));
                setShowGroupModal(false);
            }
        } catch (error) {
            toast.error(error.response?.data?.message || "Failed to create group");
        }
    };

    const handleJoinGroup = async () => {
        if (!joinCode.trim()) {
            toast.error("Please enter a group code");
            return;
        }
        
        try {
            const response = await axiosConfig.post(API_ENDPOINTS.JOIN_GROUP, { groupId: joinCode });
            if (response.status === 200) {
                toast.success("Joined group successfully!");
                setUser(prev => ({...prev, groupId: response.data.groupId}));
                setShowGroupModal(false);
            }
        } catch (error) {
            toast.error(error.response?.data?.message || "Failed to join group");
        }
    };

    const handleInvite = async () => {
        if (!inviteEmail.trim()) {
            toast.error("Please enter an email address");
            return;
        }

        try {
            const response = await axiosConfig.post(API_ENDPOINTS.INVITE_MEMBER, { email: inviteEmail });
            if (response.status === 200) {
                toast.success("Invitation sent!");
                setInviteEmail("");
            }
        } catch (error) {
            toast.error(error.response?.data?.message || "Failed to send invitation");
        }
    };

    // --- NEW: Handle Budget Update ---
    const handleUpdateBudget = async () => {
        if (!budgetAmount || isNaN(budgetAmount) || Number(budgetAmount) <= 0) {
            toast.error("Please enter a valid amount");
            return;
        }

        try {
            const response = await axiosConfig.put(API_ENDPOINTS.UPDATE_BUDGET, { amount: budgetAmount });
            if (response.status === 200) {
                toast.success("Budget updated successfully!");
                setUser(prev => ({...prev, budgetLimit: response.data.budgetLimit}));
                setShowBudgetModal(false);
            }
        } catch (error) {
            toast.error("Failed to update budget");
        }
    };

    return (
        <div className="h-[61px] bg-white border-b border-gray-200/50 w-full flex items-center justify-between px-6 sticky top-0 z-30">
            <div className="flex items-center gap-2 cursor-pointer" onClick={() => navigate("/home")}>
                <div className="p-2 bg-purple-100 rounded-xl">
                    <PiggyBank className="text-purple-600 w-6 h-6" />
                </div>
                {/* FIXED: Changed Name to RupeeRoot */}
                <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-purple-600 to-purple-900">
                    RupeeRoot
                </span>
            </div>

            <div className="flex items-center gap-4">
                {/* --- BUDGET BUTTON --- */}
                <button 
                    onClick={() => setShowBudgetModal(true)}
                    className="hidden md:flex items-center gap-2 px-4 py-2 bg-green-50 text-green-700 rounded-lg hover:bg-green-100 transition-colors border border-green-200"
                >
                    <Wallet size={18} />
                    <span className="font-medium text-sm">
                        {user?.budgetLimit ? `Budget: ₹${user.budgetLimit}` : "Set Budget"}
                    </span>
                </button>

                {/* Group Button */}
                <button 
                    onClick={() => setShowGroupModal(true)}
                    className="hidden md:flex items-center gap-2 px-4 py-2 bg-gray-50 text-gray-700 rounded-lg hover:bg-gray-100 transition-colors border border-gray-200"
                >
                    <Users size={18} />
                    <span className="font-medium text-sm">
                        {user?.groupId ? `Group: ${user.groupId}` : "Create/Join Group"}
                    </span>
                </button>

                <div className="hidden md:flex items-center gap-3 pl-4 border-l border-gray-200">
                    <div className="text-right hidden lg:block">
                        <p className="text-sm font-semibold text-gray-900">{user?.fullName}</p>
                        <p className="text-xs text-gray-500">{user?.email}</p>
                    </div>
                    
                    <div className="relative" ref={dropdownRef}>
                        <button 
                            onClick={() => setShowDropdown(!showDropdown)}
                            className="w-10 h-10 rounded-full bg-gray-100 border-2 border-white shadow-sm overflow-hidden hover:ring-2 hover:ring-purple-100 transition-all"
                        >
                            {user?.profileImageUrl ? (
                                <img src={user.profileImageUrl} alt="profile" className="w-full h-full object-cover" />
                            ) : (
                                <User className="w-full h-full p-2 text-gray-500" />
                            )}
                        </button>

                        {showDropdown && (
                            <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-lg border border-gray-100 py-1 animate-in fade-in slide-in-from-top-2">
                                <div className="px-4 py-3 border-b border-gray-100 lg:hidden">
                                    <p className="text-sm font-semibold text-gray-900">{user?.fullName}</p>
                                    <p className="text-xs text-gray-500 truncate">{user?.email}</p>
                                </div>
                                <button 
                                    onClick={handleLogout}
                                    className="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                                >
                                    <LogOut size={16} />
                                    Logout
                                </button>
                            </div>
                        )}
                    </div>
                </div>

                {/* Mobile Menu Button */}
                <button 
                    className="md:hidden p-2 hover:bg-gray-100 rounded-lg"
                    onClick={() => setOpenSideMenu(true)}
                >
                    <Menu size={24} className="text-gray-600" />
                </button>
            </div>

            {/* --- BUDGET MODAL --- */}
            {showBudgetModal && (
                <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden animate-in zoom-in-95">
                        <div className="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
                            <h3 className="font-semibold text-gray-900">Set Monthly Budget</h3>
                            <button onClick={() => setShowBudgetModal(false)} className="p-1 hover:bg-gray-200 rounded-full transition-colors">
                                <X size={20} className="text-gray-500" />
                            </button>
                        </div>
                        <div className="p-6 space-y-6">
                             <div>
                                <label className="block text-sm font-medium text-gray-700 mb-2">Monthly Limit (₹)</label>
                                <input 
                                    type="number" 
                                    className="w-full bg-gray-50 border border-gray-300 rounded-lg px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent outline-none transition-all"
                                    placeholder="e.g. 20000"
                                    value={budgetAmount}
                                    onChange={(e) => setBudgetAmount(e.target.value)}
                                />
                                <p className="text-xs text-gray-500 mt-2">
                                    We'll notify you via email if your monthly expenses exceed this amount.
                                </p>
                            </div>
                            
                            <button 
                                onClick={handleUpdateBudget}
                                className="w-full bg-green-600 text-white px-4 py-3 rounded-lg hover:bg-green-700 font-medium shadow-sm transition-all flex items-center justify-center gap-2"
                            >
                                <Wallet size={18} />
                                Save Budget
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* Group Modal */}
            {showGroupModal && (
                <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center p-4">
                    <div className="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden animate-in zoom-in-95">
                        <div className="px-6 py-4 border-b border-gray-100 flex justify-between items-center bg-gray-50">
                            <h3 className="font-semibold text-gray-900">Family Group</h3>
                            <button onClick={() => setShowGroupModal(false)} className="p-1 hover:bg-gray-200 rounded-full transition-colors">
                                <X size={20} className="text-gray-500" />
                            </button>
                        </div>
                        
                        <div className="p-6 space-y-6">
                            {user?.groupId ? (
                                <div className="space-y-4">
                                    <div className="bg-purple-50 p-4 rounded-xl border border-purple-100 text-center">
                                        <p className="text-sm text-purple-600 mb-1 font-medium">Your Group Code</p>
                                        <p className="text-3xl font-bold text-purple-900 tracking-wider">{user.groupId}</p>
                                    </div>
                                    
                                    <div className="relative">
                                        <div className="absolute inset-0 flex items-center">
                                            <div className="w-full border-t border-gray-200"></div>
                                        </div>
                                        <div className="relative flex justify-center text-sm">
                                            <span className="px-2 bg-white text-gray-500">Invite Member</span>
                                        </div>
                                    </div>

                                    <div className="flex gap-2">
                                        <input 
                                            type="email" 
                                            placeholder="Enter email address"
                                            className="flex-1 bg-gray-50 border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-purple-500 focus:border-transparent outline-none"
                                            value={inviteEmail}
                                            onChange={(e) => setInviteEmail(e.target.value)}
                                        />
                                        <button 
                                            onClick={handleInvite}
                                            className="bg-purple-600 text-white px-4 py-2.5 rounded-lg hover:bg-purple-700 font-medium transition-colors"
                                        >
                                            Invite
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                <div className="space-y-6">
                                    <button 
                                        onClick={handleCreateGroup}
                                        className="w-full bg-purple-600 text-white px-6 py-3 rounded-xl hover:bg-purple-700 font-medium shadow-lg shadow-purple-200 transition-all flex items-center justify-center gap-2"
                                    >
                                        <Users size={20} />
                                        Create New Group
                                    </button>
                                    
                                    <div className="relative">
                                        <div className="absolute inset-0 flex items-center">
                                            <div className="w-full border-t border-gray-200"></div>
                                        </div>
                                        <div className="relative flex justify-center text-sm">
                                            <span className="px-2 bg-white text-gray-500">Or join existing</span>
                                        </div>
                                    </div>

                                    <div className="flex flex-col gap-3">
                                        <label className="text-sm font-medium text-gray-700">Have a code?</label>
                                        <div className="flex gap-2">
                                            <input 
                                                type="text" 
                                                placeholder="Enter 8-character code"
                                                className="flex-1 bg-gray-50 border border-gray-300 rounded-lg px-4 py-2.5 focus:ring-2 focus:ring-gray-800 focus:border-transparent outline-none uppercase font-medium"
                                                value={joinCode}
                                                onChange={(e) => setJoinCode(e.target.value)}
                                            />
                                            <button 
                                                onClick={handleJoinGroup}
                                                className="bg-gray-900 text-white px-6 py-2.5 rounded-lg hover:bg-black font-medium shadow-sm transition-all"
                                            >
                                                Join
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* Mobile Side Menu */}
            {openSideMenu && (
                <div className="fixed top-16 left-0 right-0 bottom-0 bg-black/50 backdrop-blur-sm z-20 lg:hidden" onClick={() => setOpenSideMenu(false)}>
                    <div className="bg-white w-64 h-full shadow-xl overflow-y-auto" onClick={(e) => e.stopPropagation()}>
                        <Sidebar activeMenu={activeMenu} closeMenu={() => setOpenSideMenu(false)} />
                    </div>
                </div>
            )}
        </div>
    );
};

export default Menubar;