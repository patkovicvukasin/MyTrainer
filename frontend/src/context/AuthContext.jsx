import React, { createContext, useState, useEffect } from 'react';
const AuthContext = createContext();
export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem('jwt'));
  useEffect(() => { if (token) localStorage.setItem('jwt', token); }, [token]);
  const logout = () => { setToken(null); localStorage.removeItem('jwt'); };
  return <AuthContext.Provider value={{ token, setToken, logout }}>{children}</AuthContext.Provider>;
}
export default AuthContext;