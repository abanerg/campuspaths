import React, {Component} from 'react';
import Map, {Edge} from "./Map";
import UserInteract from "./UserInteract";
// Allows us to write CSS styles inside App.css, any styles will apply to all components inside <App />
import "./App.css";

interface AppState {
    edgesToAdd: Edge[]; // App has a list of all the edges it needs to give to Map at all times
    // When this list changes, so does the map
}

class App extends Component<{}, AppState> { // <- {} means no props.
    static readonly defaultZoomLevel: number = 15;
    constructor(props: any) { // Init App
        super(props);
        this.state = {
            edgesToAdd: [], // Empty list of edges
        };
    }
    onDraw = (edges: Edge[]) => { // Behavior to be passed to EdgeList when Find Path or Reset is pressed
        if (edges.length === 0) { // If empty array is given
            this.setState((state, props) => ({
                edgesToAdd: [], // Clear out whole map
            }));
        }
        else {
            this.setState((state, props) => ({
                edgesToAdd: state.edgesToAdd.concat(edges) // Add new route to map
            }));
        }
    }

    render() {
        return (
            <div>
                <h1 id="app-title">Campus Paths</h1>
                <div id="mapDiv">
                    <Map edges={this.state.edgesToAdd}/> {
                    // Give Map list of edges for it to render
                    // When state updates from onDraw, so will Map
                }
                </div>
                <UserInteract onChange={this.onDraw}/> {
                // UserInteract prop function is the defined onDraw function
                // UserInteract knows the param and return type of the function already,
                // but we're supplying specific behavior to it now.
            }
            </div>
        );
    }
}

export default App;
