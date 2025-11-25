import { useState, useEffect, useContext } from "react";
import EmojiPickerPopup from "./EmojiPickerPopup.jsx";
import Input from "./Input.jsx";
import { AppContext } from "../context/AppContext.jsx"; // Import AppContext

// Add 'categories' prop
const AddExpenseForm = ({ onAddExpense, categories }) => {
    // Get user from context to check for group status
    const { user } = useContext(AppContext);

    const [expense, setExpense] = useState({ 
        name: "",
        categoryId: "", 
        amount: "",
        date: "",
        icon: "", 
        isSplittable: true, // --- NEW FIELD with default TRUE ---
    });

    useEffect(() => {
        if (categories && categories.length > 0 && !expense.categoryId) {
            setExpense((prev) => ({ ...prev, categoryId: categories[0].id }));
        }
    }, [categories, expense.categoryId]);

    const handleChange = (key, value) => setExpense({ ...expense, [key]: value }); 

    const categoryOptions = categories.map((cat) => ({
        value: cat.id, 
        label: `${cat.name}`, 
    }));

    return (
        <div className="space-y-4"> {/* Added space-y-4 for better structure */}
            <EmojiPickerPopup
                icon={expense.icon}
                onSelect={(emoji) => handleChange("icon", emoji)}
            />
            
            <Input
                value={expense.name}
                onChange={({ target }) => handleChange("name", target.value)}
                label="Expense Name" // Changed Income Source to Expense Name
                placeholder="e.g., Electricity, Wifi"
                type="text"
            />

            <Input
                label="Category"
                value={expense.categoryId}
                onChange={({ target }) => handleChange("categoryId", target.value)}
                isSelect={true}
                options={categoryOptions}
            />

            <Input
                value={expense.amount}
                onChange={({ target }) => handleChange("amount", target.value)}
                label="Amount"
                placeholder="e.g., 150.00"
                type="number"
            />

            <Input
                value={expense.date}
                onChange={({ target }) => handleChange("date", target.value)}
                label="Date"
                placeholder=""
                type="date"
            />

            {/* --- NEW CHECKBOX (Optional Split) --- */}
            {user?.groupId && ( // Only show if the user is in a group
                <div className="flex items-center pt-2">
                    <input
                        id="isSplittable"
                        type="checkbox"
                        checked={expense.isSplittable}
                        onChange={(e) => handleChange("isSplittable", e.target.checked)}
                        className="w-4 h-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                    />
                    <label htmlFor="isSplittable" className="ml-2 block text-sm text-gray-700 font-medium">
                        Split this expense with the group (Debts/Settlements)
                    </label>
                </div>
            )}
            {/* ------------------------------------- */}

            <div className="flex justify-end pt-4">
                <button
                    type="button"
                    className="add-btn add-btn-fill"
                    onClick={() => onAddExpense(expense)}
                >
                    Add Expense
                </button>
            </div>
        </div>
    );
};

export default AddExpenseForm;