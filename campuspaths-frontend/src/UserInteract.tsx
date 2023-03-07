import React, {ChangeEvent, Component} from 'react';
import {Edge} from './Map'


interface UserInteractProps {
    onChange(edges: Edge[]): void; // Defined type of prop onChange, will be supplied by parent
                                   // Logic that EdgeList will use when one of the buttons are pressed
}

interface UserInteractState {
    buildings: JSX.Element[] // Array of the dropdown options of long building names
    startingBuilding: string // Current starting building to find path of
    endingBuilding: string // Current ending building to find path of
}

/**
 * A text field that allows the user to enter the list of edges.
 * Also contains the buttons that the user will use to interact with the app.
 */

class UserInteract extends Component<UserInteractProps, UserInteractState> {
    static readonly squareWidth: number = 10;

    constructor(props: UserInteractProps) { // Init EdgeList
        super(props);
        this.state = { // Set starting values, with no buildings populated yet and no valid building locations for path
            buildings: [<option value={"NULL"}>NO BUILDING SELECTED</option>],
            startingBuilding: "NULL",
            endingBuilding: "NULL",
        };
    }

    async componentDidMount() { // Once this UserInteract component is loaded
        try {
            let response = await fetch("http://localhost:4567/get-valid-buildings"); // Send fetch request and wait
            if (!response.ok) { // If response is not valid
                alert("Error.");
                return;
            }
            let parsedResponse = await response.json();
            let shortNames: string[] = Object.keys(parsedResponse); // Get keys of json map
            let buildingMap: Map<string, string> = new Map<string, string>(); // Make new map
            for (let shortName of shortNames) { // For every key in the map, make TS map using key and value
                buildingMap.set(shortName, parsedResponse[shortName])
            }
            let buildingsToBeAdded: JSX.Element[] = []; // Make new array of HTML elements
            // Sort array by maps values, so long names will be in alphabetical order
            const sortedBuildings = new Map([...buildingMap.entries()].sort((a, b) => a[1].localeCompare(b[1])));
            buildingsToBeAdded.push(<option key={"NULL"} value={"NULL"}>NO BUILDING SELECTED</option>); // Add initial NULL option
            let keyNum: number = 0;
            for (let building of sortedBuildings) { // Add option for every building, with short name as internal value
                buildingsToBeAdded.push(<option key={keyNum} value={building[0]}>{building[1]}</option>); // and long name as display
                keyNum++;
            }
            this.setState({buildings: buildingsToBeAdded}); // Update state
        } catch (e) { // Server down
            alert("Server is not running.");
        }
    }

    onDraw = async () => { // Function for when Find Path button is pressed
        // If valid buildings are selected (i.e. not NULL) or not a self node (self node would send back empty array
        // which would clear whole map due to Reset button logic
        try {
            if (!(this.state.startingBuilding === "NULL" || this.state.endingBuilding === "NULL") &&
                !(this.state.startingBuilding === this.state.endingBuilding)) {
                let response = await fetch("http://localhost:4567/find-path?start=" + this.state.startingBuilding + "&end="
                    + this.state.endingBuilding); // Send fetch request with valid params for Path json
                if (!response.ok) { // If response is not valid
                    alert("Error.");
                    return;
                }
                let parsedPathData = await response.json();
                let edgesForThisPath: Edge[] = []; // Make new empty array of Edges
                let pathList: any = parsedPathData.path; // Get path field of Path json object
                for (let i in pathList) { // For every path segment
                    let pathSegment: any = pathList[i];
                    edgesForThisPath.push({ // Take pathSegment fields and push an Edge to array with that data
                        x1: pathSegment["start"]["x"],
                        y1: pathSegment["start"]["y"],
                        x2: pathSegment["end"]["x"],
                        y2: pathSegment["end"]["y"],
                        color: "purple",
                    });
                }
                // Take paths and concat with square edge route at start and end of paths
                if (pathList.length > 0) { // If not a self node
                    edgesForThisPath = edgesForThisPath.concat(this.makeSquare(pathList[0]["start"]["x"],
                        pathList[0]["start"]["y"])).concat(this.makeSquare(pathList[pathList.length - 1]["end"]["x"],
                        pathList[pathList.length - 1]["end"]["y"]))
                }
                this.props.onChange(edgesForThisPath); // Send Edges to parent to be drawn
            }
        } catch (e) { // Server down
            alert("Server is not running.");
        }
    }
    makeSquare = (X: number, Y: number) => { // Makes black squares for beginning and ends of paths
        let offset: number = UserInteract.squareWidth / 2; // Offset from center is half of width
        let squareEdges: Edge[] = [];
        for (let i = 0; i < UserInteract.squareWidth; i++) { // Fill square with "stripes" of edges
            squareEdges.push({ // TL to BL
                x1: X - offset + i,
                y1: Y + offset,
                x2: X - offset + i,
                y2: Y - offset,
                color: "black"
            })
        }
        return squareEdges;
    }

    reset = () => { // Clearing Map and dropdown logic for "Reset" button
        let select: any = document.querySelector('#startSelect'); // Find select start building element
        select.value = 'NULL'; // Set value to NULL option
        select = document.querySelector('#endSelect') // Find select end building element
        select.value = 'NULL'; // Set value to NULL option
        this.setState((state, props) => {
            this.setState({
                startingBuilding: "NULL",
                endingBuilding: "NULL",
            })
        })
        this.props.onChange([]); // Send empty edges list to be rendered, clearing the map
    }

    render() {
        return (
            <div id="dropdowns">
                <div id="start">
                    <p>Starting Building</p>
                    <select name="start-select" id="startSelect" onChange={(e) =>
                        this.setState({startingBuilding: e.target.value})}>
                        {this.state.buildings}
                        {
                            // When option is changed, set starting building value to the new option's value
                        }
                    </select>
                </div>
                <div id="end">
                    <p>Ending Building</p>
                    <select name="end-select" id="endSelect" onChange={(e: ChangeEvent<HTMLSelectElement>) =>
                        this.setState({endingBuilding: e.target.value})}>
                        {this.state.buildings}
                        {
                            // When option is changed, set ending building value to the new option's value
                        }
                    </select>
                </div>
                <div><br/></div>
                <div>
                    <button id="draw-path" onClick={this.onDraw}>Find Path</button>
                    {
                        // Give behavior when finding path to Find Path Button
                    }
                    <button id="reset-path" onClick={this.reset}>Reset</button>
                    {
                        // Give behavior on resetting to Reset button
                    }
                </div>
            </div>
        );
    }
}

export default UserInteract;
