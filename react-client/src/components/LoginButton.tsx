import React from "react";
import { useAuth } from "../auth/AuthContext.tsx";

export const LoginButton: React.FC = () => {
    const { login, loading } = useAuth();

    if (loading) {
        return (
            <div className={"w-20 h-9 bg-gray-200 rounded-lg animate-pulse"}></div>
        );
    }

    return (
        <button
            onClick={login}
            className={"bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition duration-200 font-medium"}>
            Sign in
        </button>
    )
}