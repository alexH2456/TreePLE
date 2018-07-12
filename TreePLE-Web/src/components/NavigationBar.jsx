import React, {PureComponent} from 'react';
import {Icon, Image, Statistic, Table} from 'semantic-ui-react';
import IconMenu from './IconMenu';
import {getTreePLESustainability} from './Requests';
import Logo from '../images/treeple_logo.png';

class NavigationBar extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      sustainability: {
        stormwater: {factor: 0, worth: 0},
        co2Reduced: {factor: 0, worth: 0},
        biodiversity: {factor: 0, worth: 0},
        energyConserved: {factor: 0, worth: 0}
      },
      showSidebar: false,
      error: ''
    };
  }

  componentWillMount() {
    getTreePLESustainability()
      .then(({data}) => {
        this.setState({
          sustainability: {
            stormwater: data.stormwater,
            co2Reduced: data.co2Reduced,
            biodiversity: data.biodiversity,
            energyConserved: data.energyConserved
          }
        });
      })
      .catch(({response: {data}}) => {
        this.setState({error: data.message});
      });
  }

  toggleSidebar = () => this.setState({showSidebar: !this.state.showSidebar});

  onSustainabilityChange = (sustainability) => this.setState({sustainability: sustainability});

  render() {
    const {sustainability} = this.state;

    const stormwater = sustainability.stormwater ? sustainability.stormwater : {factor: '--', worth: '--'};
    const co2Reduced = sustainability.co2Reduced ? sustainability.co2Reduced : {factor: '--', worth: '--'};
    const biodiversity = sustainability.biodiversity ? sustainability.biodiversity : {factor: '--'};
    const energyConserved = sustainability.energyConserved ? sustainability.energyConserved : {factor: '--', worth: '--'};

    return (
      <div>
        <Table basic fixed size='small' textAlign='center'>
          <Table.Header>
            <Table.Row verticalAlign='middle'>
              <Table.HeaderCell rowSpan={2}>
                <Image centered size='small' src={Logo} onClick={this.toggleSidebar}/>
              </Table.HeaderCell>
              <Table.HeaderCell colSpan={1}>
                <Icon name='tree' size='large'/>
                <Statistic label='Biodiversity Index'/></Table.HeaderCell>
              <Table.HeaderCell colSpan={2}>
                <Icon name='tint' size='large'/>
                <Statistic label='Stormwater Intercepted'/>
              </Table.HeaderCell>
              <Table.HeaderCell colSpan={2}>
                <Icon name='cloud' size='large'/>
                <Statistic label='CO2 Reduced'/>
              </Table.HeaderCell>
              <Table.HeaderCell colSpan={2}>
                <Icon name='lightning' size='large'/>
                <Statistic label='Energy Conserved'/>
              </Table.HeaderCell>
            </Table.Row>
            <Table.Row>
              <Table.HeaderCell>
                <Statistic horizontal size='mini' value={biodiversity.factor.toFixed(5)}/>
              </Table.HeaderCell>

              <Table.HeaderCell>
                <Statistic horizontal size='mini' label='L/year' value={stormwater.factor.toFixed(2)}/>
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Statistic horizontal size='mini' label='$' value={stormwater.worth.toFixed(2)}/>
              </Table.HeaderCell>

              <Table.HeaderCell>
                <Statistic horizontal size='mini' label='kg/year' value={co2Reduced.factor.toFixed(2)}/>
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Statistic horizontal size='mini' label='$' value={co2Reduced.worth.toFixed(2)}/>
              </Table.HeaderCell>

              <Table.HeaderCell>
                <Statistic horizontal size='mini' label='kWh/year' value={energyConserved.factor.toFixed(2)}/>
              </Table.HeaderCell>
              <Table.HeaderCell>
                <Statistic horizontal size='mini' label='$' value={energyConserved.worth.toFixed(2)}/>
              </Table.HeaderCell>
            </Table.Row>
          </Table.Header>
        </Table>
        <IconMenu show={this.state.showSidebar} onSustainabilityChange={this.onSustainabilityChange}/>
      </div>
    );
  }
}

export default NavigationBar;
