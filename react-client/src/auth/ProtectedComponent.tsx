import React from "react";
import { useAuth } from "./AuthContext.tsx";

interface ProtectedComponentProps {
    children: React.ReactNode;
    fallback?: React.ReactNode
}

export const ProtectedComponent: React.FC<ProtectedComponentProps> = ({children, fallback}) => {
    const { isAuthenticated, login, loading } = useAuth();

    if (loading) {
        return (
            <div className={"flex justify-center items-center p-8"}>
                <div className={"animate-spin rounded-full h-8 w-8 border-b-2 border-green-500"}></div>
            </div>
        )
    }

    if (!isAuthenticated) {
        if (fallback) {
            return fallback;
        }
        return (
            <div className={"text-center p-8 bg-white rounded-lg shadow-md max-w-md mx-auto"}>
                <h3 className={"text-xl font-semibold text-gray-800 mb-4"}>
                    Sign in required
                </h3>
                <p className={"text-gray-600 mb-6"}>
                    Please sign in to stream music and access premium features.
                </p>
                <button
                    onClick={login}
                    className={"bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition duration-200 font-medium"}>
                    Sign in
                </button>
            </div>
        );
    }
    return <>{children}</>;
};