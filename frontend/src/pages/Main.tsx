import { Navigate } from "react-router-dom";
import { InfoHolder } from "../types";
import { GlobalError } from "../components/GlobalError";

const Main = ({ infoHolder }: { infoHolder: InfoHolder }) => {
    if (!infoHolder.info.login) {
        return <Navigate to="/login" />
    }

    if (!infoHolder.info.canAccess && !infoHolder.info.canManage) {
        return <GlobalError title="Forbidden" message="Ask organizer to give you access." />;
    }

    return (
        <>
            <p>Активный контест: <strong>{infoHolder.info.contestName}</strong></p>
        </>
    );
};

export default Main;