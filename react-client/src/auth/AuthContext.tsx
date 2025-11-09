import React, {createContext, useCallback, useContext, useEffect, useState} from "react";
import { keycloak, initKeycloak} from "./keycloak.ts";
import {userApiService, type UserProfile} from "../services/UserApi.ts";

interface AuthContextType {
    isAuthenticated: boolean;
    user: any;
    userProfile: UserProfile | null;
    login: () => void;
    logout: () => void;
    register: () => void;
    hasRole: (roles: string[]) => boolean;
    loading: boolean;
    syncUserProfile: () => Promise<void>;
    refreshUserProfile: () => Promise<void>;
    updateDisplayName: (displayName: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode}> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [user, setUser] = useState<any>(null);
    const [userProfile, setUserProfile] = useState<UserProfile | null>(null);
    const [loading, setLoading] = useState(true);

    const syncUserProfile = useCallback(async (): Promise<void> => {
        if (!keycloak.authenticated) return;

        try {
            const profile = await userApiService.syncUser();
            setUserProfile(profile);
        } catch (error) {
            throw new Error('Failed to sync user profile');
        }
    }, [])

    const refreshUserProfile = useCallback(async (): Promise<void> => {
        if (!keycloak.authenticated) return;

        try{
            const profile = await userApiService.getCurrentUser();
            setUserProfile(profile);
        } catch (error) {
            throw new Error('Failed to refresh user profile');
        }
    }, []);

    const updateDisplayName = useCallback(async (displayName: string): Promise<void> => {
        try {
            const updatedProfile = await userApiService.updateDisplayName(displayName);
            setUserProfile(updatedProfile);
        } catch (error) {
            throw new Error('Failed to update display name');
        }
    }, [])

    const initializeAuth = useCallback(async (): Promise<void> => {
        try{
            const authenticated = await initKeycloak();
            setIsAuthenticated(authenticated)

            if (authenticated && keycloak.tokenParsed) {
                setUser(keycloak.tokenParsed);
                await syncUserProfile();
            }

            keycloak.onAuthSuccess = () => {
                setIsAuthenticated(true);
                if (keycloak.tokenParsed) {
                    console.log('User:', keycloak.tokenParsed);
                    setUser(keycloak.tokenParsed);
                }
                syncUserProfile();
            };

            keycloak.onAuthLogout = () => {
                setIsAuthenticated(false);
                setUser(null);
                setUserProfile(null);
            };

            keycloak.onTokenExpired = () => {
                keycloak.updateToken(30).then(refreshed => {
                    if (refreshed) {
                        console.log('Token refreshed');
                    } else {
                        console.log('Token refresh failed');
                    }
                }).catch(() => {
                    keycloak.login();
                });
            }
        } catch (error) {
            console.error('Error initializing Keycloak:', error);
            setIsAuthenticated(false);
        } finally {
            setLoading(false);
        }
    }, [syncUserProfile]);

    useEffect(() => {
        initializeAuth();
    }, [initializeAuth]);

    const login = useCallback(() : void => {
        keycloak.login();
    }, []);

    const logout = useCallback((): void => {
        keycloak.logout();
    }, []);

    const register = useCallback((): void => {
        keycloak.register();
    }, []);

    const hasRole = useCallback((roles: string[]) => {
        if (!keycloak.authenticated || !keycloak.tokenParsed) return false;

        const userRoles = keycloak.tokenParsed.realm_access?.roles || [];
        return roles.some(role => userRoles.includes(role));
    }, []);

    const authContextValues: AuthContextType = {
        isAuthenticated,
        user,
        userProfile,
        login,
        logout,
        register,
        hasRole,
        loading,
        syncUserProfile,
        refreshUserProfile,
        updateDisplayName
    }

    return (
        <AuthContext.Provider value={authContextValues}>
            {children}
        </AuthContext.Provider>
    );
};

export const
    useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};