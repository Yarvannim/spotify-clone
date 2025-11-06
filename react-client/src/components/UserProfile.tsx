import React from "react";
import { useAuth } from "../auth/AuthContext.tsx";

export const UserProfile: React.FC = () => {
    const { user, logout, isAuthenticated } = useAuth();

    if (!isAuthenticated || !user) {
        return null
    }

    return (
        <div className={"flex items-center gap-4 bg-white rounded-full px-4 py-2 shadow-sm"}>
            <div className={"flex items-center gap-2"}>
                <div className={"w-8 h-8 bg-green-600 rounded-full flex items-center justify-center"}>
                    <span className={"text-white text-sm font-medium"}>
                        {user.preferred_username?.[0]?.toUpperCase() || user.name?.[0]?.toUpperCase() || 'U'}
                    </span>
                </div>
                <span className={"text-gray-700 text-sm font-medium"}>
                    {user.preferred_username || user.name || 'User'}
                </span>
            </div>
            <button
                onClick={logout}
                className={"text-gray-500 hover:text-gray-700 text-sm transition duration-200"}
                title={"Logout"}>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
            </button>
        </div>
    );
};