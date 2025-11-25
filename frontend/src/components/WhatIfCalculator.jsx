import { useState, useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Calculator, TrendingDown, TrendingUp, DollarSign, Plus, Trash2, RefreshCw } from 'lucide-react';
import { addThousandsSeparator } from '../util/util.js';

const WhatIfCalculator = ({ totalIncomeThisMonth = 0, totalExpenseThisMonth = 0 }) => {
    const [expenses, setExpenses] = useState([
        { id: 1, amount: '', description: '', isNew: true }
    ]);

    // Functions to manage expense list
    const addExpense = () => {
        const newExpense = {
            id: Date.now(),
            amount: '',
            description: '',
            isNew: true
        };
        setExpenses(prev => [...prev, newExpense]);
    };

    const removeExpense = (id) => {
        setExpenses(prev => prev.filter(expense => expense.id !== id));
    };

    const updateExpense = (id, field, value) => {
        setExpenses(prev => prev.map(expense => 
            expense.id === id 
                ? { ...expense, [field]: value, isNew: false }
                : expense
        ));
    };

    const resetCalculator = () => {
        setExpenses([{ id: Date.now(), amount: '', description: '', isNew: true }]);
    }

    // Calculate results with useMemo
    const calculations = useMemo(() => {
        const totalHypotheticalExpenses = expenses.reduce((sum, expense) => {
            const amount = parseFloat(expense.amount) || 0;
            return sum + amount;
        }, 0);
        
        const currentBalance = totalIncomeThisMonth - totalExpenseThisMonth;
        const newBalance = currentBalance - totalHypotheticalExpenses;
        const newTotalExpenses = totalExpenseThisMonth + totalHypotheticalExpenses;

        return {
            currentBalance,
            newBalance,
            newTotalExpenses,
            totalHypotheticalExpenses,
            expenseCount: expenses.filter(e => parseFloat(e.amount) > 0).length
        };
    }, [expenses, totalIncomeThisMonth, totalExpenseThisMonth]);

    // Animation variants
    const cardVariants = {
        hidden: { opacity: 0, y: 20 },
        visible: { opacity: 1, y: 0, transition: { duration: 0.4 } }
    };

    // Animated number component
    const AnimatedNumber = ({ value, prefix = "₹", isNegative = false, label }) => {
        const displayValue = addThousandsSeparator(Math.abs(Math.round(value)));
        
        return (
            <div className="text-center p-3 bg-gray-50 rounded-xl border border-gray-100">
                <p className="text-xs text-gray-500 mb-1 uppercase tracking-wide font-semibold">{label}</p>
                <motion.div 
                    className={`text-xl font-bold ${isNegative || value < 0 ? 'text-red-600' : 'text-gray-900'}`}
                    key={value}
                    initial={{ scale: 0.8, opacity: 0 }}
                    animate={{ scale: 1, opacity: 1 }}
                >
                    {value < 0 ? '-' : ''}{prefix}{displayValue}
                </motion.div>
            </div>
        );
    };

    return (
        <motion.div
            variants={cardVariants}
            initial="hidden"
            animate="visible"
            className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 h-full flex flex-col"
        >
            {/* Header */}
            <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-3">
                    <div className="w-10 h-10 bg-purple-100 rounded-full flex items-center justify-center text-purple-600">
                        <Calculator size={20} />
                    </div>
                    <div>
                        <h3 className="text-lg font-bold text-gray-800">"What If" Scenario</h3>
                        <p className="text-xs text-gray-500">Simulate expenses</p>
                    </div>
                </div>
                <button 
                    onClick={resetCalculator}
                    className="text-gray-400 hover:text-gray-600 transition-colors"
                    title="Reset"
                >
                    <RefreshCw size={16} />
                </button>
            </div>

            {/* Input Section */}
            <div className="space-y-3 mb-6 flex-grow overflow-y-auto max-h-[200px] pr-1 custom-scrollbar">
                <AnimatePresence mode='popLayout'>
                    {expenses.map((expense) => (
                        <motion.div
                            key={expense.id}
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: 20 }}
                            layout
                            className="flex gap-2 items-center"
                        >
                            <div className="relative flex-1">
                                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">₹</span>
                                <input
                                    type="number"
                                    placeholder="0"
                                    value={expense.amount}
                                    onChange={(e) => updateExpense(expense.id, 'amount', e.target.value)}
                                    className="w-full pl-7 pr-3 py-2 rounded-lg border border-gray-200 focus:border-purple-500 focus:ring-2 focus:ring-purple-100 outline-none text-sm transition-all"
                                />
                            </div>
                            
                            <input
                                type="text"
                                placeholder="For what?"
                                value={expense.description}
                                onChange={(e) => updateExpense(expense.id, 'description', e.target.value)}
                                className="flex-[1.5] px-3 py-2 rounded-lg border border-gray-200 focus:border-purple-500 focus:ring-2 focus:ring-purple-100 outline-none text-sm transition-all"
                            />
                            
                            {expenses.length > 1 && (
                                <button
                                    onClick={() => removeExpense(expense.id)}
                                    className="p-2 text-gray-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-colors"
                                >
                                    <Trash2 size={16} />
                                </button>
                            )}
                        </motion.div>
                    ))}
                </AnimatePresence>
                
                <button
                    onClick={addExpense}
                    className="w-full py-2 border border-dashed border-gray-300 text-gray-500 text-sm rounded-lg hover:border-purple-400 hover:text-purple-600 hover:bg-purple-50 transition-all flex items-center justify-center gap-2"
                >
                    <Plus size={16} /> Add Item
                </button>
            </div>

            {/* Results Section - Only shows when data is entered */}
            <AnimatePresence>
                {calculations.totalHypotheticalExpenses > 0 && (
                    <motion.div 
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: "auto" }}
                        exit={{ opacity: 0, height: 0 }}
                        className="space-y-4 border-t pt-4 border-gray-100"
                    >
                        {/* Impact Badge */}
                        <div className={`flex items-center justify-center gap-2 py-2 rounded-lg text-sm font-medium ${
                            calculations.newBalance < 0 
                                ? 'bg-red-100 text-red-700' 
                                : 'bg-green-100 text-green-700'
                        }`}>
                            {calculations.newBalance < 0 ? (
                                <>
                                    <TrendingDown size={16} />
                                    Budget Deficit!
                                </>
                            ) : (
                                <>
                                    <TrendingUp size={16} />
                                    Safe to Spend
                                </>
                            )}
                        </div>

                        <div className="grid grid-cols-2 gap-3">
                            <AnimatedNumber 
                                value={calculations.currentBalance}
                                label="Current"
                            />
                            <AnimatedNumber 
                                value={calculations.newBalance}
                                label="After Spending"
                                isNegative
                            />
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </motion.div>
    );
};

export default WhatIfCalculator;