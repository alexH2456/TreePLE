import React, {PureComponent} from 'react';
import {compose, withProps} from 'recompose';
import {Button, Divider, Dropdown, Header, Flag, Form, Grid, Icon, Modal} from 'semantic-ui-react';
import {GoogleMap, Marker, withScriptjs, withGoogleMap} from 'react-google-maps';
import DayPickerInput from 'react-day-picker/DayPickerInput';
import {getAllTrees, createForecast} from "./Requests";
import {formatDate} from './Utils';
import {gmapsKey, mtlCenter, huDates} from '../constants';


class CreateTreeModal extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      user: localStorage.getItem('username'),
      forecast: {
        fcDate: new Date(),
        fcTrees: []
      },
      hover: null,
      trees: [],
      treesSelected: []
    };
  }

  componentWillMount() {
    getAllTrees()
      .then(({data}) => {
        this.setState({trees: data});
      })
      .catch(error => {
        console.log(error);
      });
  }

  onCreateForecast = () => {
    const fcParams = {
      fcUser: this.state.user,
      fcDate: formatDate(this.state.forecast.fcDate),
      fcTrees: this.state.updatedTree
    };

    createForecast(fcParams)
      .then(({data}) => {
        this.props.onClose(null);
      })
      .catch(error => {
        console.log(error);
      })
  }

  onRemoveSelectedTree = (tree) => {

  }

  onTreeClick = ({treeId}) => {
    let treesSelected = this.state.treesSelected.slice();
    if (!treesSelected.includes(treeId)) {
      treesSelected.push(treeId);
      this.setState({treesSelected: treesSelected});
    }
  }

  onTreeRightClick = (tree) => {

  }

  onDateChange = (day, {selected}) => this.setState(prevState => ({tree: {...prevState.tree, datePlanted: day}}));
  onFlagChange = (e, {value}) => this.setState({language: value});

  render() {
    const {trees, treesSelected} = this.state;

    const flags = [
      {key: 'en', value: 'en', text: <Flag name='ca'/>},
      {key: 'hu', value: 'hu', text: <Flag name='hu'/>}
    ];

    const dayPickerProps = {
      locale: this.state.language,
      weekdaysShort: this.state.language == 'hu' ? huDates.weekShort : undefined,
      weekdaysLong: this.state.language == 'hu' ? huDates.weekLong : undefined,
      months: this.state.language == 'hu' ? huDates.months : undefined
    };

    return (
      <Modal open size='large' dimmer='blurring'>
        <Modal.Content>
          <Modal.Header>
            <Header as='h1' icon textAlign='center'>
              <Icon name='wpforms' circular/>
              <Header.Content>Create Forecast</Header.Content>
            </Header>
          </Modal.Header>
          <Modal.Description>
            <Form>
              <Form.Group widths='equal'>
                <Form.Input label='Date Planted'>
                  <Dropdown compact selection options={flags} defaultValue={flags[0].key} onChange={this.onFlagChange}/>
                  <DayPickerInput placeholder='YYYY-MM-DD' format='YYYY-M-D' value={this.state.forecast.fcDate} dayPickerProps={dayPickerProps} onDayChange={this.onDateChange}/>
                </Form.Input>
                <Form.Input fluid readOnly label='Total Trees' placeholder='Number of Trees' type='number' min='0' value={treesSelected.length}/>
              </Form.Group>
            </Form>

            <Header as='h3' textAlign='center'>
              <Header.Content content='Tree Map'/>
            </Header>
            <Divider/>
            <Grid centered>
              <Grid.Row>
                <Button inverted color='blue' size='small' disabled={!this.state.user} onClick={this.props.toggleForecast}>Edit</Button>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateForecast}>Save</Button>
                {/* <Dropdown inverted color='orange' size='small' onClick={this.onToggleEdit}>Back</Dropdown> */}
              </Grid.Row>
            </Grid>

            <Divider hidden/>
            <GMap trees={trees} hover={this.state.hover} onTreeClick={this.onTreeClick} onTreeRightClick={this.onTreeRightClick}/>
            <Divider hidden/>

            <Grid centered>
              <Grid.Row>
                <Button inverted color='green' size='small' disabled={!this.state.user} onClick={this.onCreateForecast}>Create</Button>
                <Button inverted color='orange' size='small' onClick={this.props.onForecast}>Back</Button>
                <Button inverted color='red' size='small' onClick={this.props.onClose}>Close</Button>
              </Grid.Row>
            </Grid>
          </Modal.Description>
        </Modal.Content>
      </Modal>
    );
  }
}

const GMap = compose(
  withProps({
    googleMapURL: `https://maps.googleapis.com/maps/api/js?key=${gmapsKey}&v=3.exp&libraries=geometry,drawing,places`,
    loadingElement: <div style={{width: '100vw', height: '40vh'}}/>,
    containerElement: <div style={{height: '40vh'}}/>,
    mapElement: <div style={{height: '40vh'}}/>,
  }),
  withScriptjs,
  withGoogleMap
)(({trees, hover, onTreeClick, onTreeRightClick}) => {

  return (
    <GoogleMap zoom={14} center={mtlCenter}>
      {trees.map(tree => {
        return (
          <Marker
            key={tree.treeId}
            position={{lat: tree.location.latitude, lng: tree.location.longitude}}
            onClick={() => onTreeClick(tree)}
            onRightClick={() => onTreeRightClick(tree)}
          >
            {(!!hover && hover == tree) ? (
              <InfoWindow></InfoWindow>
            ) : null}
          </Marker>
        );
      })}
    </GoogleMap>
  );
});

export default CreateTreeModal;
