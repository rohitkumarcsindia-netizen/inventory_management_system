"use client";
import { motion, AnimatePresence } from "framer-motion";

export default function AlertPopup({
  show,
  message,
  type = "success", // success | error | info
  onClose,
}) {
  return (
    <AnimatePresence>
      {show && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 bg-black/40 flex items-center justify-center z-50"
        >
          <motion.div
            initial={{ scale: 0.7, y: -40 }}
            animate={{ scale: 1, y: 0 }}
            exit={{ scale: 0.7, y: -40 }}
            transition={{ duration: 0.3 }}
            className="bg-white rounded-xl p-6 w-[360px] shadow-2xl text-center"
          >
            <h2
              className={`text-xl font-bold mb-2 ${
                type === "success"
                  ? "text-green-600"
                  : type === "error"
                  ? "text-red-600"
                  : "text-blue-600"
              }`}
            >
              {type === "success"
                ? "✅ Success"
                : type === "error"
                ? "❌ Error"
                : "ℹ️ Info"}
            </h2>

            <p className="text-gray-700 mb-5">{message}</p>

            <button
              onClick={onClose}
              className={`px-6 py-2 rounded-lg text-white font-medium ${
                type === "success"
                  ? "bg-green-500 hover:bg-green-600"
                  : type === "error"
                  ? "bg-red-500 hover:bg-red-600"
                  : "bg-blue-500 hover:bg-blue-600"
              }`}
            >
              OK
            </button>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}
