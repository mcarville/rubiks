import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      queryResponse: {},
	  filter: {}
    };
  }

  loadElasticSearchResults() {
	  
	  var filter = this.state.filter;
	  var query = (this.isNotEmpty(filter)) 
		? {"term" : filter}
		: { "match_all": {} };
	  
	  fetch("logstash-2018.06.28/_search", {
		  method: 'POST',
		  headers: {
			Accept: 'application/json',
			'Content-Type': 'application/json',
			},
			body: JSON.stringify({
			"query": query,
			"aggs" : {
				"userbrowser.keyword" : {"terms" : { "field" : "userbrowser.keyword" }},
				"geoip.city_name.keyword" : {"terms" : { "field" : "geoip.city_name.keyword" }}
			}
		  })
	  })
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            isLoaded: true,
            queryResponse: result
          });
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            isLoaded: true,
            error
          });
        }
      )
  }
  
  componentDidMount() {
    this.loadElasticSearchResults()
  }

  isNotEmpty (object) {
	  return object != null && Object.keys(object).length > 0;
  }
  
  filterOnAggregation = (aggregationKey, itemKey) => {
	  console.log(aggregationKey + " => " + itemKey);
	  
	  var currentFilter = {};
	  if(this.state.filter == null || this.state.filter[aggregationKey] != itemKey) {
		currentFilter[aggregationKey] = itemKey;
	  }
	  
	  this.state.filter = currentFilter;
	  
	  this.loadElasticSearchResults();
  }
  
  render() {
    
	var queryResponse = this.state.queryResponse;
	
	return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <p className="App-intro">
          To get started, edit <code>src/App.js</code> and save to reload.
        </p>
		
		{(this.isNotEmpty (queryResponse) ) ? (
			<div style={{overflow: 'auto'}}>
				<div style={{float: 'left', width: '30%'}}>
					{(this.isNotEmpty (queryResponse.aggregations) ) ? (
						<div>
							{Object.keys(queryResponse.aggregations).map((aggregationKey, bucket) => (
								<div>
								<div key={{aggregationKey}} style={{backgroundColor: '#ddd'}}>
									{aggregationKey}
								</div>
								<div>
								{queryResponse.aggregations[aggregationKey].buckets.map((item,i) => (
									<div key={item.key} style={{cursor: 'pointer', fontWeight: (this.state.filter[aggregationKey] == item.key) ? 'bold' : 'normal'}} onClick={(e) => this.filterOnAggregation(aggregationKey, item.key)} >
										{item.key} ({item.doc_count})
									</div>
								))}
								</div>
								</div>
							))}
						</div>
					) : (
						<div>No aggregations</div>
					)}
				</div>
				<div style={{float: 'left', width: '70%'}}>
					{(this.isNotEmpty (queryResponse.hits.hits) ) ? (
						<div>
							<div style={{overflow: 'auto', lineHeight: '40px', fontSize: '18px'}}>
								<div style={{float: 'left', width: '30%'}}>{queryResponse.hits.total} </div>
								<div style={{float: 'left', width: '30%'}}>{queryResponse.hits.max_score} </div>
								<div style={{float: 'left', width: '30%'}}>{queryResponse.hits.hits.length} </div>
							</div>
							<div>
								{queryResponse.hits.hits.map((item,i) => (
									<div key={item['_id']} style={{overflow: 'auto'}}>
										<div style={{float: 'left', width: '30%'}}>{item['_id']} </div>
										<div style={{float: 'left', width: '30%'}}>{item['_source'].geoip.city_name} </div>
										<div style={{float: 'left', width: '30%'}}>{item['_source'].userbrowser}</div>
									</div>
								))}
							</div>
						</div>
					) : (<div>No results</div>)}
				</div>
			</div>)
			:
			(<div>Nothing to show</div>)
		}
		
      </div>
    );
  }
}

export default App;
