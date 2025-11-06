import React, {createContext, useCallback, useContext, useEffect, useState} from "react";
import { keycloak, initKeycloak} from "./keycloak.ts";

interface AuthContextType {
    isAuthenticated: boolean;
    user: any;
    login: () => void;
    logout: () => void;
    hasRole: (roles: string[]) => boolean;
    loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode}> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [user, setUser] = useState<any>(null);
    const [loading, setLoading] = useState(true);

    const initializeAuth = useCallback(async (): Promise<void> => {
        try{
            const authenticated = await initKeycloak();
            setIsAuthenticated(authenticated)

            if (authenticated && keycloak.tokenParsed) {
                setUser(keycloak.tokenParsed);
            }

            keycloak.onAuthSuccess = () => {
                setIsAuthenticated(true);
                if (keycloak.tokenParsed) {
                    setUser(keycloak.tokenParsed);
                }
            };

            keycloak.onAuthLogout = () => {
                setIsAuthenticated(false);
                setUser(null);
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
    }, []);

    useEffect(() => {
        initializeAuth();
    }, [initializeAuth]);

    const login = useCallback(() : void => {
        keycloak.login();
    }, []);

    const logout = useCallback((): void => {
        keycloak.logout();
    }, []);

    const hasRole = useCallback((roles: string[]) => {
        if (!keycloak.authenticated || !keycloak.tokenParsed) return false;

        const userRoles = keycloak.tokenParsed.realm_access?.roles || [];
        return roles.some(role => userRoles.includes(role));
    }, []);

    const authContextValues: AuthContextType = {
        isAuthenticated,
        user,
        login,
        logout,
        hasRole,
        loading,
    }

    return (
        <AuthContext.Provider value={authContextValues}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};