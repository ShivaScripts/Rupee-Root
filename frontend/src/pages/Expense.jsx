import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import {useUser} from "../hooks/useUser.jsx";
import axiosConfig from "../util/axiosConfig.jsx";
import {API_ENDPOINTS} from "../util/apiEndpoints.js";
import Dashboard from "../components/Dashboard.jsx";
import ExpenseOverview from "../components/ExpenseOverview.jsx";
import ExpenseList from "../components/ExpenseList.jsx";
import Modal from "../components/Modal.jsx";
import AddExpenseForm from "../components/AddExpenseForm.jsx";
import DeleteAlert from "../components/DeleteAlert.jsx";

const Expense = () => {
    useUser();
    const navigate = useNavigate();
    const [expenseData, setExpenseData] = useState([]);
    const [categories, setCategories] = useState([]); 
    const [loading, setLoading] = useState(false);
    const [openAddExpenseModal, setOpenAddExpenseModal] = useState(false);
    const [openDeleteAlert, setOpenDeleteAlert] = useState({
        show: false,
        data: null,
    });

    const fetchExpenseDetails = async () => {
        if (loading) return; 

        setLoading(true);

        try {
            const response = await axiosConfig.get(
                `${API_ENDPOINTS.GET_ALL_EXPENSE}`
            );

            if (response.data) {
                setExpenseData(response.data);
            }
        } catch (error) {
            toast.error("Failed to fetch expense details");
        } finally {
            setLoading(false);
        }
    };

    const fetchCategories = async () => {
        try {
            const response = await axiosConfig.get(
                API_ENDPOINTS.CATEGORY_BY_TYPE("EXPENSE")
            );
            if (response.status === 200) {
                setCategories(response.data);
            }
        } catch (e) {
            console.error("Error fetching categories:", e);
            toast.error("Failed to load categories");
        }
    };

    useEffect(() => {
        fetchExpenseDetails();
        fetchCategories();
    }, []);

    const handleAddExpense = async (expense) => {
        // Basic validation
        if (!expense.name || !expense.amount || !expense.categoryId || !expense.date) {
            toast.error("Please fill all the fields");
            return;
        }

        try {
            const response = await axiosConfig.post(API_ENDPOINTS.ADD_EXPENSE, {
                name: expense.name,
                amount: expense.amount,
                icon: expense.icon,
                categoryId: expense.categoryId,
                date: expense.date,
                // --- FIX: Pass the isSplittable flag ---
                isSplittable: expense.isSplittable 
                // ---------------------------------------
            });

            if (response.status === 201) {
                toast.success("Expense added successfully");
                setOpenAddExpenseModal(false);
                fetchExpenseDetails();
            }
        } catch (error) {
            toast.error("Failed to add expense");
        }
    };

    const deleteExpense = async (id) => {
        try {
            await axiosConfig.delete(API_ENDPOINTS.DELETE_EXPENSE(id));
            toast.success("Expense deleted successfully");
            setOpenDeleteAlert({ show: false, data: null });
            fetchExpenseDetails();
        } catch (error) {
            toast.error("Failed to delete expense");
        }
    };

    const handleDownloadExpenseDetails = async () => {
        try {
            const response = await axiosConfig.get(
                API_ENDPOINTS.EXPENSE_EXCEL_DOWNLOAD,
                { responseType: "blob" }
            );
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", "expense_details.xlsx");
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (error) {
            toast.error("Failed to download expense details");
        }
    };

    const handleEmailExpenseDetails = async () => {
        try {
            const response = await axiosConfig.get(API_ENDPOINTS.EMAIL_EXPENSE);
            if (response.status === 200) {
                toast.success("Expense details sent to your email");
            }
        } catch (error) {
            toast.error("Failed to send email");
        }
    };

    return (
        <Dashboard>
            <div className="flex flex-col gap-y-6 md:gap-y-10">
                {/* --- RESTORED GREEN BUTTON FUNCTIONALITY --- */}
                {/* Passed onAdd to Overview so the existing green button works */}
                <ExpenseOverview 
                    transactions={expenseData} 
                    onAdd={() => setOpenAddExpenseModal(true)} 
                />

                <div className="flex flex-col gap-y-4">
                    <div className="flex justify-between items-center">
                        <h1 className="text-2xl font-bold text-slate-800">Transactions</h1>
                        {/* --- REMOVED PURPLE BUTTON FROM HERE --- */}
                    </div>

                    <ExpenseList
                        transactions={expenseData}
                        onDelete={(id) => {
                            setOpenDeleteAlert({ show: true, data: id });
                        }}
                        onDownload={handleDownloadExpenseDetails}
                        onEmail={handleEmailExpenseDetails}
                    />

                    <Modal
                        isOpen={openAddExpenseModal}
                        onClose={() => setOpenAddExpenseModal(false)}
                        title="Add Expense"
                    >
                        <AddExpenseForm
                            onAddExpense={handleAddExpense}
                            categories={categories} 
                        />
                    </Modal>

                    <Modal
                        isOpen={openDeleteAlert.show}
                        onClose={() => setOpenDeleteAlert({ show: false, data: null })}
                        title="Delete Expense"
                    >
                        <DeleteAlert
                            content="Are you sure you want to delete this expense detail?"
                            onDelete={() => deleteExpense(openDeleteAlert.data)}
                        />
                    </Modal>
                </div>
            </div>
        </Dashboard>
    );
};

export default Expense;