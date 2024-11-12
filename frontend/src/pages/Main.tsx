import { Navigate } from "react-router-dom";
import { InfoHolder } from "../types";

const Main = ({ infoHolder }: { infoHolder: InfoHolder }) => {
    if (!infoHolder.info?.login) {
        return <Navigate to="/login" />
    }

    if (!infoHolder.info.canAccess && !infoHolder.info.canManage) {
        return (
            <div className="global-error">
                <h1>Forbidden</h1>
                <p>Ask organizer to give you access.</p>
            </div>
        )
    }

    return (
        <>
            <p>Welcome to {infoHolder.info.contestName}!</p>
        </>
    );
};

export default Main;