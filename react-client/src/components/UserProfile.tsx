import React from "react";
import { useAuth } from "../auth/AuthContext.tsx";

export const UserProfile: React.FC = () => {
    const { user, userProfile, logout } = useAuth();
    // const [isEditing, setIsEditing] = useState(false);
    // const [newDisplayName, setNewDisplayName] = useState(userProfile?.displayName || '');
    // const [isUpdating, setIsUpdating] = useState(false);

    // const handleSaveDisplayName = async () => {
    //     if (!newDisplayName.trim() || newDisplayName === userProfile?.displayName) {
    //         setIsEditing(false);
    //         return;
    //     }
    //
    //     setIsUpdating(true);
    //     try {
    //         await updateDisplayName(newDisplayName);
    //         setIsEditing(false);
    //     } catch (error) {
    //         throw new Error('Failed to update display name');
    //     } finally {
    //         setIsUpdating(false);
    //     }
    // }
    //
    // const handleCancelEdit = () => {
    //     setNewDisplayName(userProfile?.displayName || '');
    //     setIsEditing(false)
    // }

    if (!user || !userProfile) {
        return null;
    }

    return (
        <div className={"flex items-center gap-4 bg-white rounded-full px-4 py-2 shadow-sm"}>
            <div className={"flex items-center gap-2"}>
                <div className={"w-8 h-8 bg-green-600 rounded-full flex items-center justify-center"}>
                    <span className={"text-white text-sm font-medium"}>
                        {user.preferred_username?.[0]?.toUpperCase() || user.name?.[0]?.toUpperCase() || 'U'}
                    </span>
                </div>
            </div>
            {/*<div className={"flex flex-col"}>*/}
            {/*    {isEditing ? (*/}
            {/*        <div className={"flex items-center gap-2"}>*/}
            {/*            <input type={"text"} value={newDisplayName} onChange={(e) => setNewDisplayName(e.target.value)} className={"text-sm border border-gray-300 rounded px-2 py-1 focus:outline-none focus:border-green-500"} disabled={isUpdating} maxLength={50}/>*/}
            {/*            <div className={"flex gap-1"}>*/}
            {/*                <button onClick={handleSaveDisplayName} disabled={isUpdating} className={"text-green-600 hover:text-green-800 text-xs disabled:opacity-50"}>✓</button>*/}
            {/*                <button onClick={handleCancelEdit} disabled={isUpdating} className={"text-red-600 hover:text-red-800 text-xs disabled:opacity-50"}>✗</button>*/}
            {/*            </div>*/}
            {/*        </div>*/}
            {/*    ): (*/}
            {/*       <div className={"flex items-center gap-2"}>*/}
            {/*           <span className={"text-gray-700 text-sm font-medium"}>{userProfile.displayName}</span>*/}
            {/*           <button onClick={() => setIsEditing(true)} className={"text-gray-400 hover:text-gray-600 text-xs transition duration-200"} title={"Edit display name"}>✎</button>*/}
            {/*       </div>*/}
            {/*    )}*/}
            {/*    {userProfile.isArtist && (*/}
            {/*        <span className={"text-xs bg-purple-100 text-purple-800 px-2 py-0.5 rounded-full self-start"}>Artist</span>*/}
            {/*    )}*/}
            {/*</div>*/}
            <div className={"flex items-center gap-2"}>
                <span className={"text-gray-700 text-sm font-medium"}>{userProfile.displayName}</span>
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